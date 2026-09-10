package mhir.logging

import org.slf4j.Marker
import org.slf4j.event.Level

case class LogEntry(
    level: Level,
    msg: String,
    marker: Marker,
    throwable: Throwable
)
