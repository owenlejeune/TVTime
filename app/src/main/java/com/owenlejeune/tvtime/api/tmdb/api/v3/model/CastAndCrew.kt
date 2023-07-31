package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

abstract class CastAndCrew<C, R>(
    @SerializedName("cast") val cast: List<C>,
    @SerializedName("crew") val crew: List<R>
)

class TvCastAndCrew(cast: List<TvCastMember>, crew: List<TvCrewMember>): CastAndCrew<TvCastMember, TvCrewMember>(cast, crew)

class MovieCastAndCrew(cast: List<MovieCastMember>, crew: List<MovieCrewMember>): CastAndCrew<MovieCastMember, MovieCrewMember>(cast, crew)

class EpisodeCastAndCrew(
    cast: List<EpisodeCastMember>,
    crew: List<EpisodeCrewMember>,
    @SerializedName("guest_stars") val guestStars: List<EpisodeCastMember>
): CastAndCrew<EpisodeCastMember, EpisodeCrewMember>(cast, crew)