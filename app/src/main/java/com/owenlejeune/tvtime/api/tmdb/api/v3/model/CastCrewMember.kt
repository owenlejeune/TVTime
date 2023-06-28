package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.Gender

abstract class CastCrewMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("known_for_department") val knownForDepartment: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("popularity") val popularity: Float
): Person(id, name, gender, profilePath)

abstract class CastMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    @SerializedName("order") val order: Int
): CastCrewMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity)

class EpisodeCastMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    order: Int,
    @SerializedName("credit_id") val creditId: String
): CastMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity, order)

class TvCastMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    order: Int,
    @SerializedName("roles") val roles: List<CastCrewRole>,
    @SerializedName("total_episode_count") val totalEpisodeCount: Int
): CastMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity, order)

class MovieCastMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    order: Int,
    @SerializedName("cast_id") val castId: Int,
    @SerializedName("character") val character: String,
    @SerializedName("credit_id") val creditId: String
): CastMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity, order)

abstract class CrewMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    @SerializedName("department") val department: String
): CastCrewMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity)

class EpisodeCrewMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    department: String,
    @SerializedName("credit_id") val creditId: String
): CrewMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity, department)

class TvCrewMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    department: String,
    @SerializedName("total_episode_counte") val totalEpisodeCount: Int,
    @SerializedName("jobs") val jobs: List<CastCrewRole>
): CrewMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity, department)

class MovieCrewMember(
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?,
    isAdult: Boolean,
    knownForDepartment: String,
    originalName: String,
    popularity: Float,
    department: String,
    @SerializedName("job") val job: String,
    @SerializedName("credit_id") val creditId: String
): CrewMember(id, name, gender, profilePath, isAdult, knownForDepartment, originalName, popularity, department)

class CastCrewRole(
    @SerializedName("credit_id") val creditId: String,
    @SerializedName("character", alternate = ["job"]) val role: String,
    @SerializedName("episode_count") val episodeCount: Int
)