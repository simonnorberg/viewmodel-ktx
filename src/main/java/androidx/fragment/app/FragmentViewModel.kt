package androidx.fragment.app

import androidx.annotation.MainThread
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModelFactory(
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

@MainThread
inline fun <reified VM : ViewModel> Fragment.savedStateViewModelFactory(
    noinline viewModelProducer: (SavedStateHandle) -> VM
): Lazy<VM> {
    val factoryProducer: () -> ViewModelProvider.Factory = {
        object : AbstractSavedStateViewModelFactory(this, null) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return viewModelProducer(handle) as T
            }
        }
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryProducer)
}
