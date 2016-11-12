package io.connorwyatt.flashcards.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    /**
     * A method for finding the differences between two lists. Returns any items that are in the
     * source list but not in the exclude list.
     * @param sourceList The original list to get the differences for.
     * @param excludeList The values to remove.
     * @param <T> The type of the items in the lists.
     * @return A list with the differences between the source list and the exclude list.
     */
    public static <T> List<T> difference(List<T> sourceList, List<T> excludeList) {
        List<T> differenceList = new ArrayList<>(sourceList);

        differenceList.removeAll(excludeList);

        return differenceList;
    }
}
