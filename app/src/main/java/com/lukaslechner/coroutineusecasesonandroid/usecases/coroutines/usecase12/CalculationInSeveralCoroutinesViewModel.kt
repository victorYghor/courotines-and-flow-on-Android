package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase12

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.BigInteger
import java.util.Timer
import kotlin.system.measureTimeMillis

class CalculationInSeveralCoroutinesViewModel(
    private val factorialCalculator: FactorialCalculator = FactorialCalculator(),
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BaseViewModel<UiState>() {

    fun performCalculation(
        factorialOf: Int,
        numberOfCoroutines: Int
    ) {
        try {
            viewModelScope.launch {
                uiState.value = UiState.Loading

                var factorialResult = BigInteger.ONE
                val computationDuration = measureTimeMillis {
                    factorialResult = withContext(Dispatchers.IO) {
                        factorialCalculator.calculateFactorial(
                            factorialOf,
                            numberOfCoroutines
                        )
                    }
                }

                var resultString = ""
                val stringConversionDuration = measureTimeMillis {
                    resultString = withContext(Dispatchers.IO) {
                        convertToString(factorialResult)
                    }
                }
                uiState.value = UiState.Success(
                    resultString,
                    computationDuration,
                    stringConversionDuration
                )
            }
        } catch(e: Exception) {
            Timer(e.message)
            uiState.value = UiState.Error("Error while calculating result")
        }

    }

    private suspend fun convertToString(number: BigInteger): String =
        withContext(Dispatchers.IO) {
            number.toString()
        }
}