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

package com.skydoves.chamberdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.chamber.Chamber
import com.skydoves.chamberdemo.LogUtils.log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private val viewModel = MainActivityViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // inject field data and add a lifecycleOwner to the UserScope scope stack.
    Chamber.shareLifecycle(scopeOwner = viewModel, lifecycleOwner = this)

    viewModel.username.observe { LogUtils.log("observed data: $it") }

    viewModel.username.value = "skydoves on MainActivity"

    button.setOnClickListener {
      startActivity(Intent(this, SecondActivity::class.java))
    }

    button2.setOnClickListener {
      LogUtils.log("property data: ${viewModel.username.value}")
    }
  }
}
