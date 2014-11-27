Tweets = new Meteor.Collection("geoTweet");

if (Meteor.isClient) {

  Meteor.startup(function() {
    Session.set("toggleSentiment","display: none");
    Session.set("tweetType", "apple");
    Session.set("sentiment", false);
    
    //Blue - positive
    gradient1 = [
    'rgba(0, 255, 255, 0)',
    'rgba(0, 255, 255, 1)',
    'rgba(0, 225, 255, 1)',
    'rgba(0, 200, 255, 1)',
    'rgba(0, 175, 255, 1)',
    'rgba(0, 160, 255, 1)',
    'rgba(0, 145, 223, 1)',
    'rgba(0, 125, 191, 1)',
    'rgba(0, 110, 255, 1)',
    'rgba(0, 100, 255, 1)',
    'rgba(0, 75, 255, 1)',
    'rgba(0, 50, 255, 1)',
    'rgba(0, 25, 255, 1)',
    'rgba(0, 0, 255, 1)'
  ]
// Red - negative
gradient2 = [
    'rgba(255, 255, 0, 0)',
    'rgba(255, 255, 0, 1)',
    'rgba(255, 225, 0, 1)',
    'rgba(255, 200, 0, 1)',
    'rgba(255, 175, 0, 1)',
    'rgba(255, 160, 0, 1)',
    'rgba(255, 145, 0, 1)',
    'rgba(255, 125, 0, 1)',
    'rgba(255, 110, 0, 1)',
    'rgba(255, 100, 0, 1)',
    'rgba(255, 75, 0, 1)',
    'rgba(255, 50, 0, 1)',
    'rgba(255, 25, 0, 1)',
    'rgba(255, 0, 0, 1)'
  ]
});


togglePosHeatmap =function() {
  heatmap1.setMap(heatmap1.getMap() ? null : map);  
}

toggleNegHeatmap =function() {  
  heatmap2.setMap(heatmap2.getMap() ? null : map);    
}



changeRadius=function() {
  heatmap1.set('radius', heatmap1.get('radius') ? null : 20);
  heatmap2.set('radius', heatmap2.get('radius') ? null : 20);
}

changeOpacity = function () {
  heatmap1.set('opacity', heatmap1.get('opacity') ? null : 0.2);
  heatmap2.set('opacity', heatmap2.get('opacity') ? null : 0.2);
}


Template.body.helpers({

   tweets: function(){    


  var sentiment = Session.get('sentiment');
  var tweetType = Session.get('tweetType');

   var isMap = Session.get('map');

  if(isMap && tweetType) {
        var tweetType = Session.get('tweetType');
        if(sentiment){
            var allTweets = Tweets.find({trackName: tweetType, sentiment:{$exists: true}});
        }else{
            var allTweets = Tweets.find({trackName: tweetType});        
      }
        pointArray1.clear();
        pointArray2.clear();
        var posCount = 0;
        var negCount = 0;
        allTweets.forEach(function (tweet) {            
            if(tweet.sentiment == "POSITIVE"){                
               posCount++;
                pointArray1.push(new google.maps.LatLng(tweet.latitude, tweet.longitude));                
             }else{       
                negCount++;       
                pointArray2.push(new google.maps.LatLng(tweet.latitude, tweet.longitude));
             }   
            
        });
    }
  if(typeof tweetType === "undefined" || typeof isMap === "undefined"){
    return 0;
  }
  
  if(isMap && sentiment){    
    heatmap1.set('gradient', gradient1);  
    heatmap2.set('gradient', gradient2); 
    var totalCount = (posCount+negCount);
    var overallPos = false;
    var overallNeg = false;    

    if(posCount>negCount){
      var overallPos = true;
    }else if(posCount<negCount){
      var overallNeg = true;
    }

    var totalCountObj = {pos: posCount, neg:negCount, total: totalCount, sentiment: true, overallPos: overallPos, overallNeg: overallNeg}    
  }else{
    heatmap1.set('gradient', null);  
    heatmap2.set('gradient', null);  
    var totalCount = (posCount+negCount);
    var totalCountObj = {total: totalCount, sentiment: false}
    
  }
  
  return totalCountObj;
  },
  toggleSentiment: function () {
    return Session.get("toggleSentiment");
  }
  });



Template.mapPostsList.rendered = function() {  
  
  var sanFrancisco = new google.maps.LatLng(37.774546, -122.433523);
  map = new google.maps.Map(document.getElementById('map-canvas'), {
      center: sanFrancisco,
      zoom: 1,
      mapTypeId: google.maps.MapTypeId.SATELLITE
  });

  var heatmapData1 = [
  ];
  var heatmapData2 = [
  ];

  pointArray1 = new google.maps.MVCArray(heatmapData1);
  pointArray2 = new google.maps.MVCArray(heatmapData2);

  heatmap1 = new google.maps.visualization.HeatmapLayer({
      data: pointArray1
  });
  heatmap2 = new google.maps.visualization.HeatmapLayer({
      data: pointArray2
  });
  
  heatmap1.setMap(map); 
  heatmap2.setMap(map); 

  Session.set('map', true);  
  Session.set("posGradient", "blue");
};

Template.mapPostsList.destroyed = function() {
 Session.set('map', false);
};

Template.body.events({
  "change .select-type select": function(evt){
  var newValue = $(evt.target).val();
  Session.set("tweetType", newValue);
  },
  "click .toggle-map": function(evt){  
    Session.set("sentiment", Session.get('sentiment') ? false : true);
    if(Session.get("toggleSentiment")==""){
      Session.set("toggleSentiment","display: none");      
    }else{
      Session.set("toggleSentiment","");      
    }
  }


});




}

// On server startup, create some players if the database is empty.
if (Meteor.isServer) {
  
}
