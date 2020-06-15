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

    // collection of available meeting times
    Collection<TimeRange> openTimes = new ArrayList<>();

    // initial available time: the entire day
    int start = TimeRange.START_OF_DAY;
    int end = TimeRange.END_OF_DAY;

    for (Event event: eventsArray) {
      TimeRange tryTime = TimeRange.fromStartEnd(start, end, true);

      if (tryTime.overlaps(event.getWhen())) {
        // get lists of event attendees
        Collection<String> requestedEventAttendees = request.getAttendees();
        Set<String> scheduledEventAttendees = event.getAttendees();
        
        for (String attendee: requestedEventAttendees) {
          // check for overlapping attendees
          if (scheduledEventAttendees.contains(attendee)) {
            int newEnd = event.getWhen().start();

            // see if meeting can be scheduled between start and conflicting event
            if (newEnd - start >= request.getDuration()) {
              openTimes.add(TimeRange.fromStartEnd(start, newEnd, false));
            }

            start = event.getWhen().end();
            break;
          }
        }
      }
    }

    // see if meeting can be scheduled from start to end of day
    if (end - start >= request.getDuration()) {
      openTimes.add(TimeRange.fromStartEnd(start, end, true));
    }

    return openTimes;

  }
}
