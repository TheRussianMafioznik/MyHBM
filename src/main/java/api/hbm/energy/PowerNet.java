package api.hbm.energy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.hbm.config.GeneralConfig;

import api.hbm.energy.IEnergyConnector.ConnectionPriority;
import net.minecraft.tileentity.TileEntity;

public class PowerNet implements IPowerNet {

	private boolean valid = true;
	private HashMap<Integer, IEnergyConductor> links = new HashMap<Integer, IEnergyConductor>();
	private HashMap<Integer, Integer> proxies = new HashMap<Integer, Integer>();
	private List<IEnergyConnector> subscribers = new ArrayList<IEnergyConnector>();
	private Set<IEnergyConnector> subscriberSet = Collections.newSetFromMap(new IdentityHashMap<IEnergyConnector, Boolean>());

	private static final ConnectionPriority[] PRIORITIES = new ConnectionPriority[] {
		ConnectionPriority.HIGH,
		ConnectionPriority.NORMAL,
		ConnectionPriority.LOW
	};

	public static List<PowerNet> trackingInstances = null;
	protected long totalTransfer = 0;

	@Override
	public void joinNetworks(IPowerNet network) {
		if (network == this)
			return;

		for (IEnergyConductor conductor : network.getLinks()) {
			joinLink(conductor);
		}
		network.getLinks().clear();

		for (IEnergyConnector connector : network.getSubscribers()) {
			this.subscribe(connector);
		}

		network.destroy();
	}

	@Override
	public IPowerNet joinLink(IEnergyConductor conductor) {
		if (conductor.getPowerNet() != null)
			conductor.getPowerNet().leaveLink(conductor);

		conductor.setPowerNet(this);
		int identity = conductor.getIdentity();
		this.links.put(identity, conductor);

		if (conductor.hasProxies()) {
			for (Integer i : conductor.getProxies()) {
				this.proxies.put(i, identity);
			}
		}

		return this;
	}

	@Override
	public void leaveLink(IEnergyConductor conductor) {
		conductor.setPowerNet(null);
		int identity = conductor.getIdentity();
		this.links.remove(identity);

		if (conductor.hasProxies()) {
			for (Integer i : conductor.getProxies()) {
				this.proxies.remove(i);
			}
		}
	}

	@Override
	public void subscribe(IEnergyConnector connector) {
		if (connector == null) {
			return;
		}
		if (this.subscriberSet.add(connector)) {
			this.subscribers.add(connector);
		}
	}

	@Override
	public void unsubscribe(IEnergyConnector connector) {
		if (connector != null && this.subscriberSet.remove(connector)) {
			this.subscribers.removeIf(x -> x == connector);
		}
	}

	@Override
	public boolean isSubscribed(IEnergyConnector connector) {
		return connector != null && this.subscriberSet.contains(connector);
	}

	@Override
	public List<IEnergyConductor> getLinks() {
		return new ArrayList<IEnergyConductor>(this.links.values());
	}

	public HashMap<Integer, Integer> getProxies() {
		return new HashMap<Integer, Integer>(proxies);
	}

	@Override
	public List<IEnergyConnector> getSubscribers() {
		return this.subscribers;
	}

	@Override
	public void destroy() {
		this.valid = false;
		this.subscribers.clear();
		this.subscriberSet.clear();

		for (IEnergyConductor link : this.links.values()) {
			link.setPowerNet(null);
		}

		this.links.clear();
		this.proxies.clear();
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

	@Override
	public long getTotalTransfer() {
		return this.totalTransfer;
	}

	public long lastCleanup = System.currentTimeMillis();

	@Override
	public long transferPower(long power) {
		if (power <= 0 || this.subscribers.isEmpty()) {
			return power;
		}

		List<PowerNet> cache = null;
		if (trackingInstances != null && !trackingInstances.isEmpty()) {
			cache = new ArrayList<PowerNet>(trackingInstances);
		}

		if (trackingInstances == null) {
			trackingInstances = new ArrayList<PowerNet>(4);
		} else {
			trackingInstances.clear();
		}
		trackingInstances.add(this);

		long result = fairTransfer(this.subscribers, power);

		if (cache != null) {
			trackingInstances.addAll(cache);
		}
		return result;
	}

	public static void cleanup(List<IEnergyConnector> subscribers) {
		int size = subscribers.size();
		if (size == 0) return;

		if (size == 1) {
			IEnergyConnector x = subscribers.get(0);
			if (x == null || !(x instanceof TileEntity) || ((TileEntity) x).isInvalid() || !x.isLoaded()) {
				subscribers.clear();
			}
			return;
		}

		Set<IEnergyConnector> seen = Collections.newSetFromMap(new IdentityHashMap<IEnergyConnector, Boolean>(size));
		subscribers.removeIf(x ->
				x == null ||
				!(x instanceof TileEntity) ||
				((TileEntity) x).isInvalid() ||
				!x.isLoaded() ||
				!seen.add(x)
		);
	}

	public static boolean shouldSend(ConnectionPriority senderPrio, ConnectionPriority p, IEnergyConnector x) {
		return (x.getPriority() == p) && (!x.isStorage() || (senderPrio.compareTo(p) <= 0));
	}

	public static long fairTransferWithPrio(ConnectionPriority senderPrio, List<IEnergyConnector> subscribers, long power) {
		return fairTransferInternal(senderPrio, subscribers, power, true);
	}

	public static long fairTransfer(List<IEnergyConnector> subscribers, long power) {
		return fairTransferInternal(null, subscribers, power, false);
	}

	private static long fairTransferInternal(ConnectionPriority senderPrio, List<IEnergyConnector> subscribers, long power, boolean checkSenderPrio) {
		if (power <= 0) return 0;
		if (subscribers.isEmpty()) return power;

		cleanup(subscribers);
		int subCount = subscribers.size();
		if (subCount == 0) return power;

		long totalTransfer = 0;
		List<IEnergyConnector> subList = new ArrayList<IEnergyConnector>(subCount);
		long[] weights = new long[subCount];

		for (int pIdx = 0; pIdx < PRIORITIES.length; pIdx++) {
			if (power <= 0) break;
			ConnectionPriority p = PRIORITIES[pIdx];
			subList.clear();

			for (int i = 0; i < subCount; i++) {
				IEnergyConnector x = subscribers.get(i);
				boolean match = checkSenderPrio ? shouldSend(senderPrio, p, x) : (x.getPriority() == p);
				if (match) {
					subList.add(x);
				}
			}

			int matchedCount = subList.size();
			if (matchedCount == 0) continue;

			long totalReq = 0;
			for (int i = 0; i < matchedCount; i++) {
				long req = subList.get(i).getTransferWeight();
				weights[i] = req;
				totalReq += req;
			}

			if (totalReq == 0) continue;

			long totalGiven = 0;
			double invTotalReq = (double) power / (double) totalReq;

			for (int i = 0; i < matchedCount; i++) {
				IEnergyConnector con = subList.get(i);
				long given = (long) Math.floor(weights[i] * invTotalReq);
				if (given <= 0) continue;

				long leftover = con.transferPower(given);
				long accepted = given - leftover;
				if (accepted > 0) {
					totalGiven += accepted;
					if (con instanceof TileEntity) {
						TileEntity tile = (TileEntity) con;
						tile.getWorld().markChunkDirty(tile.getPos(), tile);
					}
				}
			}

			power -= totalGiven;
			totalTransfer += totalGiven;
		}

		if (trackingInstances != null) {
			for (int i = 0; i < trackingInstances.size(); i++) {
				PowerNet net = trackingInstances.get(i);
				net.totalTransfer += totalTransfer;
			}
			trackingInstances.clear();
		}

		return power;
	}

	@Override
	public void reevaluate() {
		if (!GeneralConfig.enableReEval) {
			this.destroy();
			return;
		}

		HashMap<Integer, IEnergyConductor> copy = new HashMap<Integer, IEnergyConductor>(links);
		HashMap<Integer, Integer> proxyCopy = new HashMap<Integer, Integer>(proxies);

		for (IEnergyConductor link : copy.values()) {
			this.leaveLink(link);
		}

		for (IEnergyConductor link : copy.values()) {
			link.setPowerNet(null);
			link.reevaluate(copy, proxyCopy);

			if (link.getPowerNet() == null) {
				link.setPowerNet(new PowerNet().joinLink(link));
			}
		}
	}
}