package batch

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val batchModule = module {
    singleOf(::BatchService)
}