Tweets = new Meteor.Collection("geoTweet");

if (Meteor.isClient) {

toggleHeatmap =function() {
  heatmap.setMap(heatmap.getMap() ? null : map);
}

changeGradient=function() {
  var gradient = [
    'rgba(0, 255, 255, 0)',
    'rgba(0, 255, 255, 1)',
    'rgba(0, 191, 255, 1)',
    'rgba(0, 127, 255, 1)',
    'rgba(0, 63, 255, 1)',
    'rgba(0, 0, 255, 1)',
    'rgba(0, 0, 223, 1)',
    'rgba(0, 0, 191, 1)',
    'rgba(0, 0, 159, 1)',
    'rgba(0, 0, 127, 1)',
    'rgba(63, 0, 91, 1)',
    'rgba(127, 0, 63, 1)',
    'rgba(191, 0, 31, 1)',
    'rgba(255, 0, 0, 1)'
  ]
  heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);
}

changeRadius=function() {
  heatmap.set('radius', heatmap.get('radius') ? null : 20);
}

changeOpacity = function () {
  heatmap.set('opacity', heatmap.get('opacity') ? null : 0.2);
}




Meteor.startup(function() {
    Session.set("tweetType", "apple");
});

Template.body.helpers({

   tweets: function(){

	var tweetType = Session.get('tweetType');

	 var isMap = Session.get('map');

         if(isMap && tweetType) {
    		var tweetType = Session.get('tweetType');
    		var allTweets = Tweets.find({trackName: tweetType});
    		pointArray.clear();
    		allTweets.forEach(function (tweet) {
        		pointArray.push(new google.maps.LatLng(tweet.latitude, tweet.longitude));
    		});
  	}
	if(typeof tweetType === "undefined"){
		return 0;
	}
	return Tweets.find({trackName: tweetType}).count();
  }
  });



Template.mapPostsList.rendered = function() {  
	var sanFrancisco = new google.maps.LatLng(37.774546, -122.433523);
	map = new google.maps.Map(document.getElementById('map-canvas'), {
  		center: sanFrancisco,
  		zoom: 1,
  		mapTypeId: google.maps.MapTypeId.SATELLITE
	});

	var heatmapData = [
	];

	pointArray = new google.maps.MVCArray(heatmapData);

	heatmap = new google.maps.visualization.HeatmapLayer({
  		data: pointArray
	});

	heatmap.setMap(map); 

  Session.set('map', true);
};

Template.mapPostsList.destroyed = function() {
 Session.set('map', false);
};

Template.body.events({
  "change select": function(evt){
	var newValue = $(evt.target).val();
	Session.set("tweetType", newValue);
     }
});




}

// On server startup, create some players if the database is empty.
if (Meteor.isServer) {
}
