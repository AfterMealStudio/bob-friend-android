package com.example.bob_friend_android.di

import com.example.bob_friend_android.data.repository.appointment.AppointmentRepository
import com.example.bob_friend_android.data.repository.appointment.AppointmentRepositoryImpl
import com.example.bob_friend_android.data.repository.list.ListRepository
import com.example.bob_friend_android.data.repository.list.ListRepositoryImpl
import com.example.bob_friend_android.data.repository.login.LoginRepository
import com.example.bob_friend_android.data.repository.login.LoginRepositoryImpl
import com.example.bob_friend_android.data.repository.user.UserRepository
import com.example.bob_friend_android.data.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsLoginRepository(
        repositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    abstract fun bindsListRepository(
        repositoryImpl: ListRepositoryImpl
    ): ListRepository

    @Binds
    abstract fun bindsUserRepository(
        repositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindsAppointmentRepository(
        repositoryImpl: AppointmentRepositoryImpl
    ): AppointmentRepository
}