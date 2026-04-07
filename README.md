# Emotional Maps

## Problem Statement

Emotional Maps is a system built for an imaginary startup that deploys an infrastructure for collecting user satisfaction levels at pre-determined physical locations called **Points of Interest (POIs)**. A mobile application records each user interaction, including their registration status, login status, GPS coordinates, user identifier, timestamp, and emotional state, and writes it to a flat text file.

The goal of the program is to efficiently parse large volumes of these event records and, for a user-supplied date range, produce a summary of the distribution of emotional states (angry, happy, surprised, sad, neutral) observed at each POI.

### Data format

Each line in a data file represents one event:

```
<REGISTRATION> <LOGIN> <DDMMYYYY> <USERID> <LAT,LON> <EMOTION_CODE>
```

Example:
```
IN LOGIN 15032021 abc12 45.464,9.190 A
OUT LOGOUT 20032021 xyz99 45.473,9.173 F
```

- `REGISTRATION`: `IN` (registered) or `OUT`
- `LOGIN`: `LOGIN` or `LOGOUT`
- `DDMMYYYY`: date as an 8-character string (day 2, month 2, year 4)
- `USERID`: 5-character alphanumeric identifier
- `LAT,LON`: decimal GPS coordinates
- `EMOTION_CODE`: single character — `A` (angry), `F` (happy), `S` (surprised), `T` (sad), `N` (neutral)

### POI geographic zones

Three square bounding boxes are defined around areas in Milan:

| POI  | Latitude range       | Longitude range    |
|------|----------------------|--------------------|
| POI1 | 45.459 – 45.469      | 9.185 – 9.195      |
| POI2 | 45.468 – 45.478      | 9.168 – 9.178      |
| POI3 | 45.453 – 45.463      | 9.176 – 9.185      |

Events that fall outside all three zones are labelled `UNDEFINED` and discarded.

---

## Design

### Data structure: `HashMap<Integer, TreeSet<Event>>`

The core data structure maps a calendar year to a `TreeSet` of `Event` objects for that year.

- **Insertion** — `HashMap.get(year)` is O(1); `TreeSet.add(event)` is O(log n) where n is the number of events already stored for that year. Overall: **O(log n)**.
- **Map creation** — Iterating through all events in a date range costs O(n + k), where n is the number of events within the range and k is the number of years spanned. The TreeSet's sorted order allows an early `break` once the upper-bound date is exceeded, avoiding a full scan.

### `compareTo` / `isAfter` / `isBefore`

`Event.compareTo` intentionally returns `1` (not `0`) for two events on the same day. This prevents the `TreeSet` from treating them as duplicates and silently dropping one. Because of this side-effect, `compareTo` is **not** used for date-range boundary checks; the dedicated `isAfter` and `isBefore` helpers are used instead.

---

## Solution

### Build

Requires Java 11+ and Maven 3.6+.

```bash
mvn compile
```

### Run

```bash
mvn package
java -cp target/emotional-maps-1.0-SNAPSHOT.jar emotionalmaps.EmotionalMaps
```

The program prompts for the path to a **command file**. Type `0` to exit.

### Command file format

A plain text file where each line is one of:

```
import(path/to/data-file.txt)
create_map(DDMMYYYY-DDMMYYYY)
```

Example `commands.txt`:
```
import(dati.txt)
create_map(01012020-31122020)
```

### Generate test data

```bash
java -cp target/emotional-maps-1.0-SNAPSHOT.jar emotionalmaps.RandomEventGenerator output.txt
```

Produces 100,000 random events. The output path defaults to `./test-data.txt` if no argument is provided.

---

## Testing

Unit tests are written with JUnit 5 and cover:

- `EmotionalStateTest` — `fromCode` for all valid codes and unknown input
- `EventTest` — `setDate`, `setPoi` for all POIs and UNDEFINED, `isAfter`/`isBefore` correctness, `compareTo` TreeSet deduplication prevention
- `StringParserTest` — `cutCommand` for each command type, `parseStringToEvent` for valid and malformed lines, `parseCoordinates`, `parseDateRange`
- `DataManagerTest` — `addEvent` including the year-not-in-store edge case, `createMap` for same-year and cross-year ranges, POI2/POI3 count correctness (regression for the index bug), boundary exclusion

Run all tests:
```bash
mvn test
```

---

## Performance

### In-process measurement

`DataManagerTest.benchmark_createMap_100kEvents` loads 100,000 events and measures `createMap` using `System.nanoTime()`. On a modern laptop this completes in under 25 ms.

### JMH-based benchmarking

For rigorous micro-benchmarking the recommended tool is **JMH** (Java Microbenchmark Harness). Key metrics to track:

- Average time per `createMap` call (ms) at various dataset sizes
- Throughput (ops/s) for `addEvent` under concurrent load
- GC allocation rate (`-prof gc`) — the `TreeSet`-per-year design limits GC pressure to the year being inserted into
- Warm-up latency vs. steady-state latency

See the comment block in `DataManagerTest.java` for a JMH skeleton and the required Maven dependency.
