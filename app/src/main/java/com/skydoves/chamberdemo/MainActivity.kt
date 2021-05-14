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
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.chamber.Chamber
import com.skydoves.chamberdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private val viewModel = MainActivityViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // inject field data and add a lifecycleOwner to the UserScope scope stack.
    Chamber.shareLifecycle(scopeOwner = viewModel, lifecycleOwner = this)

    viewModel.username.observe { Log.d("MainActivity", "observed data: $it") }

    viewModel.username.value = "skydoves on MainActivity"

    binding.button.setOnClickListener {
      startActivity(Intent(this, SecondActivity::class.java))
    }

    binding.button2.setOnClickListener {
      Log.d("MainActivity", "property data: ${viewModel.username.value}")
    }
  }
}
