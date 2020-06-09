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


/**
 * adds response from servlet
 */

function setComments() {
  const commentContainer = document.getElementById('comments-container');
  commentContainer.innerHTML = '';
  const loginContainer = document.getElementById('login');
  const formContainer = document.getElementById('form');
  formContainer.innerHTML = '';
  const num_comments = document.getElementById('num-comments').value;
  const order = document.getElementById('order').value;

  fetch('/data?num-comments=' + num_comments + '&order=' + order)
    .then((response) => {
      if (response.ok) {
        const contentType = response.headers.get('content-type');
        if (contentType == 'application/json') {
          response.json().then((commentsList) => {
            for (let i = 0; i < commentsList.length; i++) {
              commentContainer.appendChild(
                  createListComment(commentsList[i]));
            }
          })
          makeForm(formContainer);
          
        } else {
          response.text().then((text) => {
            const mssg = document.createElement('p');
            mssg.innerText = text;
            commentContainer.appendChild(mssg);
          });
        }
        getLoginLogout(loginContainer);
      } else {
        response.json().then((errorJson) => {
          commentContainer.appendChild(createErrorMssg(errorJson));
        });
      }
      
    })
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
  const errMssg = document.createElement('p');
  errMssg.innerText = errorJson.type + ' ERROR: ' + errorJson.message;
  return errMssg;
}

function editLoginButton(loginUrl, alreadyLoggedIn, loginAnchor) {
  loginAnchor.href = loginUrl;
  const button = loginAnchor.firstElementChild;
  if (alreadyLoggedIn == "true") {
    button.innerText = 'Log Out';
  } else {
    button.innerText = 'Log In';
  }
  //return loginAnchor;
}

function getLoginLogout(container) {
  fetch('/login', {method: 'POST'}).then((response) => {
    if (response.ok) {
      response.json()
        .then((login) => editLoginButton(login.url, login.loggedIn, container));
        //.then((button) => container.appendChild(button));
    } else {
      response.json().then(
          (errJson) => container.appendChild(createErrorMssg(errJson)));
    }
  })
}

function makeForm(container) {
  const formHtml = '<button onclick="delComments()"> Delete All Comments' +
      '</button> <br><br>' +
      '<form action="/data" method="POST"> <p> Leave a comment!' +
      '</p> <label for="name"> name/username </label>' +
      '<input type="text" id="name" name="name"></input> <br><br>' +
      '<textarea name="comment"></textarea> <br> <input type="submit"> </form>';
  container.innerHTML = formHtml;
}
