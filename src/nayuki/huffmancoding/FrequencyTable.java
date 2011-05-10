package nayuki.huffmancoding;

import java.util.PriorityQueue;
import java.util.Queue;


/**
 * A table of symbol frequencies. Mutable.
 */
public final class FrequencyTable {
	
	private int[] frequencies;
	
	
	
	public FrequencyTable(int[] freqs) {
		if (freqs == null)
			throw new NullPointerException("Argument is null");
		frequencies = freqs.clone();
		for (int x : frequencies) {
			if (x < 0)
				throw new IllegalArgumentException("Negative frequency");
		}
	}
	
	
	
	public int getSymbolLimit() {
		return frequencies.length;
	}
	
	
	public int get(int symbol) {
		if (symbol < 0 || symbol >= frequencies.length)
			throw new IllegalArgumentException("Symbol out of range");
		return frequencies[symbol];
	}
	
	
	public void set(int symbol, int freq) {
		if (symbol < 0 || symbol >= frequencies.length)
			throw new IllegalArgumentException("Symbol out of range");
		frequencies[symbol] = freq;
	}
	
	
	public void increment(int symbol) {
		if (symbol < 0 || symbol >= frequencies.length)
			throw new IllegalArgumentException("Symbol out of range");
		if (frequencies[symbol] == Integer.MAX_VALUE)
			throw new RuntimeException("Arithmetic overflow");
		frequencies[symbol]++;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < frequencies.length; i++)
			sb.append(String.format("%d\t%d%n", i, frequencies[i]));
		return sb.toString();
	}
	
	
	public CodeTree buildCodeTree() {
		Queue<NodeWithFrequency> pqueue = new PriorityQueue<NodeWithFrequency>();
		
		// Add symbol leaves with non-zero frequency
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, frequencies[i]));
		}
		
		// Pad with zero-frequency symbols until queue has at least 2 items
		for (int i = 0; pqueue.size() < 2 && i < Math.max(frequencies.length, 2); i++) {
			if (i >= frequencies.length || frequencies[i] == 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, 0));
		}
		assert pqueue.size() >= 2;
		
		// Repeatedly tie together two nodes with the lowest frequency
		while (pqueue.size() > 1) {
			NodeWithFrequency nf1 = pqueue.remove();
			NodeWithFrequency nf2 = pqueue.remove();
			pqueue.add(new NodeWithFrequency(
					new InternalNode(nf1.node, nf2.node),
					Math.min(nf1.lowestSymbol, nf2.lowestSymbol),
					nf1.frequency + nf2.frequency));
		}
		
		// Return the remaining node
		return new CodeTree((InternalNode)pqueue.remove().node, frequencies.length);
	}
	
	
	
	private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {
		
		public final Node node;
		
		public final int lowestSymbol;
		
		public final long frequency;
		
		
		public NodeWithFrequency(Node node, int lowestSymbol, long freq) {
			this.node = node;
			this.lowestSymbol = lowestSymbol;
			this.frequency = freq;
		}
		
		
		public int compareTo(NodeWithFrequency other) {
			if (frequency < other.frequency)
				return -1;
			else if (frequency > other.frequency)
				return 1;
			else if (lowestSymbol < other.lowestSymbol)
				return -1;
			else if (lowestSymbol > other.lowestSymbol)
				return 1;
			else
				return 0;
		}
		
	}
	
}
