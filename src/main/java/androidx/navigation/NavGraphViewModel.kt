package androidx.navigation

import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.fragment.findNavController

@MainThread
inline fun <reified VM : ViewModel> Fragment.navGraphViewModelFactory(
    @IdRes navGraphId: Int,
    noinline viewModelProducer: () -> VM
): Lazy<VM> {
    val backStackEntry: NavBackStackEntry by lazy {
        findNavController().getBackStackEntry(navGraphId)
    }
    val storeProducer: () -> ViewModelStore = {
        backStackEntry.viewModelStore
    }
    val factoryProducer: () -> ViewModelProvider.Factory = {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelProducer() as T
            }
        }
    }
    return ViewModelLazy(VM::class, storeProducer, factoryProducer)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.navGraphSavedStateViewModelFactory(
    @IdRes navGraphId: Int,
    noinline viewModelProducer: (SavedStateHandle) -> VM
): Lazy<VM> {
    val backStackEntry: NavBackStackEntry by lazy {
        findNavController().getBackStackEntry(navGraphId)
    }
    val storeProducer: () -> ViewModelStore = {
        backStackEntry.viewModelStore
    }
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
    return ViewModelLazy(VM::class, storeProducer, factoryProducer)
}
