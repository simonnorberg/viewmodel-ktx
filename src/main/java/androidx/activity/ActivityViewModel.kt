package androidx.activity

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelFactory(
    noinline viewModelProducer: () -> VM
): Lazy<VM> {
    val factoryProducer: () -> ViewModelProvider.Factory = {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelProducer() as T
            }
        }
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryProducer)
}
