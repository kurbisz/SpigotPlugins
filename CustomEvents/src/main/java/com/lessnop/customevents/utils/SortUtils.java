package com.lessnop.customevents.utils;

import java.util.*;

public class SortUtils {

	public static <T> void sortAndPutInMap(LinkedHashMap<T, Integer> input, LinkedHashMap<T, Integer> output) {
		List<Map.Entry<T, Integer>> entries = new ArrayList<>(input.entrySet());
		Collections.sort(entries, Comparator.comparingInt(Map.Entry::getValue));
		Collections.reverse(entries);
		output.clear();
		for(Map.Entry<T, Integer> e : entries) {
			output.put(e.getKey(), e.getValue());
		}
	}

}
