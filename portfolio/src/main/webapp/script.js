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

/**
 * Adds a random travel destination and image of destination to the page.
 */
function addRandomDestination() {
  const placesToVisit = 
      ['Tokyo', 'London', 'Rome', 'Amsterdam', 'Taipei', 'Sichuan'];

  const visit_pics = {'Tokyo': 'https://cdn.pixabay.com/photo/2020/01/15/19/03/shinjuku-4768674_960_720.jpg',
    'London': 'https://c1.wallpaperflare.com/preview/271/1022/559/westminster-palace-london-city.jpg',
    'Rome': 'https://cdn.pixabay.com/photo/2018/07/20/14/02/rome-3550739_960_720.jpg', 'Amsterdam': 'https://upload.wikimedia.org/wikipedia/commons/e/e8/Amsterdam_De_Wallen_6.jpg', 'Taipei': 'https://upload.wikimedia.org/wikipedia/commons/c/cc/Taipei_skyline_cityscape_at_night_with_full_moon.jpg', 'Sichuan': 'https://www.goodfreephotos.com/albums/china/sichuan/other/Guangfu-pavilion-with-summit-visible-in-background-in-mount-emei-sichuan-china.jpg'}

  // Pick a random destintion.
  const destination = placesToVisit[Math.floor(Math.random() * placesToVisit.length)];

  // Add it to the page.
  const travelContainer = document.getElementById('travel-container');
  

  const img = document.getElementById('visit-pic');
  img.src = visit_pics[destination];
  img.alt = 'image of ${destination}';

  travelContainer.innerText = destination;
  
}
