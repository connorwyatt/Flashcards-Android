/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.utils;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtils {
    public static <T> boolean contains(List<T> list, Predicate<T> predicate) {
        boolean result = false;
        Iterator<T> iterator = list.iterator();

        while (!result && iterator.hasNext()) {
            T listItem = iterator.next();

            result = predicate.apply(listItem);
        }

        return result;
    }

    /**
     * A method for finding the differences between two lists. Returns any items that are in the
     * source list but not in the exclude list.
     *
     * @param sourceList  The original list to get the differences for.
     * @param excludeList The values to remove.
     * @param <T>         The type of the items in the lists.
     * @return A list with the differences between the source list and the exclude list.
     */
    public static <T> List<T> difference(List<T> sourceList, List<T> excludeList) {
        List<T> differenceList = new ArrayList<>(sourceList);

        differenceList.removeAll(excludeList);

        return differenceList;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> filteredList = new ArrayList<>();

        for (T listItem : list) {
            if (predicate.apply(listItem)) {
                filteredList.add(listItem);
            }
        }

        return filteredList;
    }
}
