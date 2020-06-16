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
  commentContainer = document.getElementById('comments-container');
  commentContainer.innerHTML = '';

  num_comments = document.getElementById('num-comments').value;
  order = document.getElementById('order').value;

  const response =
      await fetch('/data?num-comments=' + num_comments + '&order=' + order);

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

let map;
function initMap() {
  const tokyo = {lat: 35.668, lng: 139.723};
  const hachi = {lat: 35.659107, lng: 139.700653};
  map = new google.maps.Map(
      document.getElementById('map'), 
      {center: tokyo, zoom: 12, mapTypeControl: false});

  const marker = new google.maps.Marker({position: hachi, map: map})

  hachiString = '<div id="hachi-content"> <h1> Statue of Hachiko </h1>' +
      '<p> a very good boi </p>' +
      '<p> For actual info on Hachiko, visit his' +
      '<a href="https://en.wikipedia.org/wiki/Hachik%C5%8D"> Wikipedia page </a>' +
      '</p> </div>';

  var infowindow = new google.maps.InfoWindow({content: hachiString});

  marker.addListener('click', function() {
    infowindow.open(map, marker);
  });

  new AutocompleteDirectionsHandler(map);
}

function addMarker() {
  const shouldAddMarker = document.getElementById('add-marker').value
  console.log(shouldAddMarker)

  if (shouldAddMarker === 'true') {
    console.log('here');
    map.addListener('click', function(mapsMouseEvent) {
      const newMarker =
          new google.maps.Marker({position: mapsMouseEvent.latLng, map: map});
    });
  }
  else {
    google.maps.event.clearListeners(map, 'click');
  }
}

// START OF DIRECTIONS CODE

// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script
// src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">


/**
 * @constructor
 */
function AutocompleteDirectionsHandler(map) {
  this.map = map;
  this.originPlaceId = null;
  this.destinationPlaceId = null;
  this.originPlace = null;
  this.destinationPlace = null;
  this.travelMode = 'WALKING';
  this.directionsService = new google.maps.DirectionsService;
  this.directionsRenderer = new google.maps.DirectionsRenderer;
  this.directionsRenderer.setMap(map);

  var originInput = document.getElementById('origin-input');
  var destinationInput = document.getElementById('destination-input');
  var modeSelector = document.getElementById('mode-selector');

  var originAutocomplete = new google.maps.places.Autocomplete(originInput);
  // Specify just the place data fields that you need.
  originAutocomplete.setFields(['place_id']);

  var destinationAutocomplete =
      new google.maps.places.Autocomplete(destinationInput);
  // Specify just the place data fields that you need.
  destinationAutocomplete.setFields(['place_id']);

  this.setupClickListener('changemode-walking', 'WALKING');
  this.setupClickListener('changemode-transit', 'TRANSIT');
  this.setupClickListener('changemode-driving', 'DRIVING');

  this.setupPlaceChangedListener(originAutocomplete, 'ORIG');
  this.setupPlaceChangedListener(destinationAutocomplete, 'DEST');

  this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(originInput);
  this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(
      destinationInput);
  this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(modeSelector);
}

// Sets a listener on a radio button to change the filter type on Places
// Autocomplete.
AutocompleteDirectionsHandler.prototype.setupClickListener = function(
    id, mode) {
  var radioButton = document.getElementById(id);
  var me = this;

  radioButton.addEventListener('click', function() {
    me.travelMode = mode;
    me.route();
  });
};

AutocompleteDirectionsHandler.prototype.setupPlaceChangedListener = function(
    autocomplete, mode) {
  var me = this;
  autocomplete.bindTo('bounds', this.map);

  autocomplete.addListener('place_changed', function() {
    var place = autocomplete.getPlace();

    if (!place.place_id) {
      let placeText;

      if (mode == 'ORIG') {
        placeText = document.getElementById('origin-input').value;
      } else {
        placeText = document.getElementById('destination-input').value;
      }

      service = new google.maps.places.PlacesService(me.map);

      var request = {
        query: placeText,
        fields: ['name', 'geometry'],
      };

      service.findPlaceFromQuery(request, function(results, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          if (mode === 'ORIG') {            
            me.originPlace = results[0];
            console.log(me.originPlace);
          } else {
            me.destinationPlace = results[0];
            console.log(me.destinationPlace);
          }

          me.route();
          return;
        }
      });
    } else {
      if (mode === 'ORIG') {
        me.originPlaceId = place.place_id;
      } else {
        me.destinationPlaceId = place.place_id;
      }
      me.route();
    }
    
  });
};

AutocompleteDirectionsHandler.prototype.route =
    function() {

  var me = this;

  let originVal;
  let destinationVal;

  console.log(this.originPlace);
  console.log(this.destinationPlace);

  if (!this.originPlace) {
    if (!this.originPlaceId) {
      return;
    } else {
      originVal = getProperPlaceVal(this.originPlaceId, true);
    }
  } else {
    originVal = getProperPlaceVal(this.originPlace, false);
  }

  if (!this.destinationPlace) {
    if (!this.destinationPlaceId) {
      return;
    } else {
      destinationVal = getProperPlaceVal(this.destinationPlaceId, true);
    }
  } else {
    destinationVal = getProperPlaceVal(this.destinationPlace, false);
  }

  this.directionsService.route(
    {
      origin: originVal,
      destination: destinationVal,
      travelMode: this.travelMode
    },
    function(response, status) {
      if (status === 'OK') {
        me.directionsRenderer.setDirections(response);
      } else {
        window.alert('Directions request failed due to ' + status);
      }
    }
  );

}

function getProperPlaceVal(locationVal, isId) {
  if (isId) {
    return {'placeId': locationVal};
  } else {
    return locationVal.name;
  }
}
