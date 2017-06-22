/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards

fun <T> List<T>.shuffle(): List<T> {
  return this.sortedBy { Math.random() }
}
