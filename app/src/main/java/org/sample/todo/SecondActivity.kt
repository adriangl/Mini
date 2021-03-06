package org.sample.todo

import android.os.Bundle
import android.widget.Toast
import com.minivac.mini.flux.FluxActivity
import com.minivac.mini.log.Grove
import javax.inject.Inject

class SecondActivity : FluxActivity<UserComponent>() {

    override fun onCreateComponentFactory() = UserComponentFactory

    @Inject lateinit var userStore: UserStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "hey: ${userStore.state.name}", Toast.LENGTH_SHORT).show()
        Grove.d { "Created" }
    }
}