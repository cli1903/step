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

  const visitPics = {
    'Tokyo': 'https://cdn.pixabay.com/photo/2020/01/15/19/03/' +
        'shinjuku-4768674_960_720.jpg',
    'London': 'https://c1.wallpaperflare.com/preview/271/1022/559/' +
        'westminster-palace-london-city.jpg',
    'Rome': 'https://cdn.pixabay.com/photo/2018/07/20/14/02/' +
        'rome-3550739_960_720.jpg',
    'Amsterdam': 'https://upload.wikimedia.org/wikipedia/commons/e/e8/' +
        'Amsterdam_De_Wallen_6.jpg',
    'Taipei': 'https://upload.wikimedia.org/wikipedia/commons/c/cc/' +
        'Taipei_skyline_cityscape_at_night_with_full_moon.jpg',
    'Sichuan': 'https://www.goodfreephotos.com/albums/china/sichuan/other/' +
        'Guangfu-pavilion-with-summit-visible-in-' +
        'background-in-mount-emei-sichuan-china.jpg'
  }

  // Pick a random travel destination.
  const placeToVisit =
      placesToVisit[Math.floor(Math.random() * placesToVisit.length)];

  // Add it to the page.
  const travelContainer = document.getElementById('travel-container');


  const img = document.getElementById('visit-pic');

  img.src = visitPics[placeToVisit];
  img.alt = 'image of ${placeToVisit}';

  travelContainer.innerText = placeToVisit;
}


async function checkLoggedIn() {
  const loginResponse = await fetch('/login', {method: 'POST'});
  const loginJson = await loginResponse.json();
  addLogInOutButton(loginJson.url, loginJson.isLoggedIn);
  console.log(loginJson.isLoggedIn);
  return loginJson.isLoggedIn;
}

/**
 * adds response from servlet
 */
async function setComments() {
  commentContainer = document.getElementById('comments-container');
  commentContainer.innerHTML = '';

  var isLoggedIn = await checkLoggedIn()
  if (isLoggedIn) {
    console.log('here');
    num_comments = document.getElementById('num-comments').value;
    order = document.getElementById('order').value;
    fetch('/data?num-comments=' + num_comments + '&order=' + order)
      .then((response) => response.json())
      .then((obj) => {
        if (num_comments >= 0 && num_comments <= 15) {
          for (let i = 0; i < obj.length; i++) {
            commentContainer.appendChild(createListComment(obj[i]));
          }
        } else {
          const errorMssg = createErrorMssg(obj);
          commentContainer.appendChild(errorMssg);
        }
      });
  } else {
    const errMssg = document.createElement('p');
    errMssg.innerText = 'Please log in to see comments.';
    commentContainer.appendChild(errMssg);
  }
  
}

function delComments() {
  fetch('/delete-data', {method: 'POST'}).then((response) => {
    contentType = response.headers.get('content-type');
    const commentContainer = document.getElementById('comments-container');
    if (contentType == 'text/html') {
      commentContainer.innerHTML = '';
    } else {
      const errorJson = response.json();
      const errorMssg = createErrorMssg(errorJson);
      commentContainer.appendChild(errorMssg);
    }
  })
}

function createListComment(comment) {
  const liElemName = document.createElement('li');
  const liElemComment = document.createElement('li');
  liElemName.innerText = comment.name + ':';
  liElemComment.innerText = comment.text;
  liElemName.appendChild(liElemComment);
  return liElemName;
}

function createErrorMssg(errorJson) {
  errMssg = document.createElement('p');
  errMssg.innerText = errorJson;
  return errMssg;
}

function addLogInOutButton(url, isLoggedIn) {
  const link = document.getElementById('login');
  link.href = url;

  const button = document.getElementById('login-button');

  if (isLoggedIn) {
    button.innerText = 'Log Out';
  } else {
    button.innerText = 'Log In';
  }
  
}
