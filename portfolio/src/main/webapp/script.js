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


async function setPage() {
  const loginResponse = await fetch('/login', {method: 'POST'});
  const loginJson = await loginResponse.json();
  addLogInOutButton(loginJson.url, loginJson.isLoggedIn);

  const customContainer = document.getElementById('custom-elements');
  const formContainer = document.getElementById('comment-form');

  if (loginJson.isLoggedIn) {
    shouldHideElement(customContainer, false);
    shouldHideElement(formContainer, false);
    setComments();

  } else {
    shouldHideElement(customContainer, true);
    shouldHideElement(formContainer, true);
    const commentContainer = document.getElementById('comments-container');
    const errElement = createErrorMssg('Please log in to see comments.');
    commentContainer.appendChild(errElement);
  }
}

/**
 * adds response from servlet
 */
async function setComments() {
  const commentContainer = document.getElementById('comments-container');
  commentContainer.innerHTML = '';

  const numCommentsElement = document.getElementById('num-comments')

  try {
    numComments = Number(numCommentsElement.value);
  } catch {
    numComments = -1;
  }

  order = document.getElementById('order').value;

  const minComments = numCommentsElement.min;
  const maxComments = numCommentsElement.max;

  if ((numComments % 1 != 0) || (numComments < minComments) ||
      (numComments > maxComments)) {
    const errString =
        'Invalid input for num-comments: please enter an integer between ' +
        minComments + ' and ' + maxComments;
        
    const errMssg = createErrorMssg(errString);
    commentContainer.appendChild(errMssg);
    return;
  }

  const response =
      await fetch('/data?num-comments=' + numComments + '&order=' + order);

  if (response.ok) {
    const responseJson = await response.json();
    for (let i = 0; i < responseJson.length; i++) {
      commentContainer.appendChild(createListComment(responseJson[i]));
    }

  } else {
    const errMssg = await response.text();
    const errElement = createErrorMssg(errMssg);
    commentContainer.appendChild(errElement);
  }
}

async function delComments() {
  const commentContainer = document.getElementById('comments-container');
  const response = await fetch('/delete-data', {method: 'DELETE'});
  if (response.ok) {
    commentContainer.innerHTML = '';
  } else {
    const errMssg = await response.text();
    const errElement = createErrorMssg(errMssg);
    commentContainer.appendChild(errElement);
  }
}

function createListComment(comment) {
  const liElemName = document.createElement('li');
  const liElemComment = document.createElement('li');
  if (comment.name == '') {
    comment.name = 'Anonymous';
  }
  liElemName.innerText = comment.name + ':';
  liElemComment.innerText = comment.text;
  liElemName.appendChild(liElemComment);
  return liElemName;
}

function createErrorMssg(errorMssg) {
  errMssg = document.createElement('p');
  errMssg.innerText = errorMssg;
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

function shouldHideElement(container, hide) {
  if (hide) {
    container.style.display = 'none';
  } else {
    container.style.display = 'block';
  }
}

function initMap() {
  const tokyo = {lat: 35.668, lng: 139.723};
  const hachi = {lat: 35.659107, lng: 139.700653};
  const map = new google.maps.Map(document.getElementById('map'), {
    center: tokyo,
    zoom: 12
  });

  const marker = new google.maps.Marker({position: hachi, map: map})

  hachiString = '<div id="hachi-content"> <h1> Statue of Hachiko </h1>' + 
    '<p> a very good boi </p>' + 
    '<p> For actual info on Hachiko, visit his' + 
    '<a href="https://en.wikipedia.org/wiki/Hachik%C5%8D"> Wikipedia page </a>' + 
    '</p> </div>';

  var infowindow = new google.maps.InfoWindow({content: hachiString});

  marker.addListener('click', function() {
    infowindow.open(map, marker);
  })
}
