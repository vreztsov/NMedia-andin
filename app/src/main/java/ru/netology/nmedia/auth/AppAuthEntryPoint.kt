package ru.netology.nmedia.auth

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.api.PostsApiService

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppAuthEntryPoint {
    fun getApiService(): PostsApiService
}