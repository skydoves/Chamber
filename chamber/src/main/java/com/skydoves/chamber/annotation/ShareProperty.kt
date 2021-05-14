/*
 * Copyright (C) 2019 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.chamber.annotation

import androidx.lifecycle.Lifecycle
import com.skydoves.chamber.ChamberProperty

/**
 * ShareProperty annotation used to register explicitly a [ChamberProperty] property
 * on the [com.skydoves.chamber.Chamber] using a specific key name: [key].
 *
 * @param key A specific key name of the [ChamberProperty] for registering on the [ChamberScope].
 *
 * @param clearOnDestroy Should be cleared or not when owner moves to the [Lifecycle.State.DESTROYED] state.
 * The all [ChamberProperty]s int the scope will be cleared and unregistered all observers
 * if the entry point of the scope moves to the [Lifecycle.State.DESTROYED] state.
 * but we can clear the [ChamberProperty] selectively if it does not be used anymore in the [ChamberScope].
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ShareProperty(val key: String, val clearOnDestroy: Boolean = false)
