/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.extensions

import android.view.ViewGroup

fun ViewGroup.getChildren() = (0 until childCount).map { getChildAt(it) }

