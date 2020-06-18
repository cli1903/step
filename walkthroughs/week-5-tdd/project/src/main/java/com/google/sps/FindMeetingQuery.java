// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method."); 

    // convert collection of events to list of events for sorting
    List<Event> eventsArray = new ArrayList<>(events);

    // comparator to sort events by starting time
    Comparator<Event> compareByEventStart = new Comparator<Event>() {
      @Override
      public int compare(Event a, Event b) {
        return Long.compare(a.getWhen().start(), b.getWhen().start());
      }
    };

    Collections.sort(eventsArray, compareByEventStart);

    Collection<String> requestedEventAttendees = request.getAttendees();

    // collection of available meeting times
    Collection<TimeRange> openTimesRequiredAttendees = new ArrayList<>();
    Collection<TimeRange> openTimesOptionalAttendees = new ArrayList<>();

    // keep track of when the next block of free time starts for requried 
    // attendees and optional attendees
    int requiredAttendeesStart = TimeRange.START_OF_DAY;
    int optionalAttendeesStart = TimeRange.START_OF_DAY;

    int end = TimeRange.END_OF_DAY;

    for (Event event: eventsArray) {
      // try largest block of free time
      TimeRange tryTime = TimeRange.fromStartEnd(requiredAttendeesStart, end, true);

      if (tryTime.overlaps(event.getWhen())) {
        // get lists of the current event attendees
        Set<String> eventAttendees = event.getAttendees();


        if (checkConflictingAttendees(requestedEventAttendees, eventAttendees)) {
          int endOfFreeTime = event.getWhen().start();
          if (endOfFreeTime - requiredAttendeesStart >= request.getDuration()) {
            openTimesRequiredAttendees.add(TimeRange.fromStartEnd(requiredAttendeesStart, endOfFreeTime, false));
          }

          if (endOfFreeTime - optionalAttendeesStart >= request.getDuration()) {
            openTimesOptionalAttendees.add(TimeRange.fromStartEnd(optionalAttendeesStart, endOfFreeTime, false));
          }

          requiredAttendeesStart = event.getWhen().end();

          if (optionalAttendeesStart < event.getWhen().end()) {
            optionalAttendeesStart = event.getWhen().end();
          }

        } else {
          // required attendees are all free, check if optional attendees are also free
          Collection<String> optionalEventAttendees = request.getOptionalAttendees();
          if (checkConflictingAttendees(optionalEventAttendees, eventAttendees)) {
            int endOfFreeTime = event.getWhen().start();
            if (endOfFreeTime - optionalAttendeesStart >= request.getDuration()) {
              openTimesOptionalAttendees.add(TimeRange.fromStartEnd(optionalAttendeesStart, endOfFreeTime, false));
            }

            optionalAttendeesStart = event.getWhen().end();
          }

        }

      }
    }

    // see if meeting can be scheduled from start to end of day
    if (end - requiredAttendeesStart >= request.getDuration()) {
      openTimesRequiredAttendees.add(TimeRange.fromStartEnd(requiredAttendeesStart, end, true));
    }

    if (end - optionalAttendeesStart >= request.getDuration()) {
      openTimesOptionalAttendees.add(TimeRange.fromStartEnd(optionalAttendeesStart, end, true));
    }

    if (openTimesOptionalAttendees.isEmpty()) {
      if (requestedEventAttendees.isEmpty()) {
        return new ArrayList<>();
      }
      return openTimesRequiredAttendees;
    } else {
      return openTimesOptionalAttendees;
    }
  }

  public boolean checkConflictingAttendees(Collection<String> eventAttendees, 
      Set<String> requestedAttendees) {

    for (String attendee: eventAttendees) {
      if (requestedAttendees.contains(attendee)) {
        return true;
      }
    }

    return false;
  }
}


