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

import com.skydoves.chamber.ChamberProperty

/**
 * [PropertyObserver] annotation used to observe value changes by [ChamberProperty] that has
 * the same [key] value. This annotation only works with a method that in a scoped class.
 * A method that is annotated with [PropertyObserver] will be invoked, whenever the value changes and
 * receive the value as a parameter. The method must have one parameter and the type must same as the generic of
 * the [ChamberProperty].
 *
 * ```
 * @ExampleScope
 * class MainClass {
 *
 *   @ShareProperty(key = "name")
 *   val name = ChamberProperty("skydoves")
 *
 *   // ... //
 *
 *   @PropertyObserver(key = "name")
 *   fun observeNameProperty(name: String) {
 *      // observe the `name` value changes.
 *   }
 * }
 *
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PropertyObserver(val key: String)
