// Use Parse.Cloud.define to define as many cloud functions as you want.

Parse.Cloud.define("initializeNewUser", function (request, response) {
    var InitialItemsObject = Parse.Object.extend("InitialParseItems");
    var itemsQuery = new Parse.Query(InitialItemsObject);
    itemsQuery.ascending("itemID");
    itemsQuery.limit(500);

    itemsQuery.find().then(function (results) {
        var user = request.user;
        console.log('Found ' + results.length + ' Items for user: ' + user.get("name"));
        var ACL = new Parse.ACL(user);
        var unsavedItemObjects = [];
        for (var i = 0; i < results.length; i++) {
            var item = new Parse.Object("Items");
            item.set('itemID', results[i].get('itemID'));
            item.set('author', user);
            item.set('groupID', results[i].get('groupID'));
            item.set('itemName', results[i].get('itemName'));
            item.set('itemNote', '');
            item.set("itemChecked", false);
            item.set("itemIsFavorite", false);
            item.set("itemSelected", false);
            item.set("itemStruckOut", false);
            item.set('manualSortOrder', results[i].get('manualSortOrder'));
            item.set('productID', -1);
            item.setACL(ACL);
            unsavedItemObjects[i] = item;
        }

        console.log('Ready to save ' + unsavedItemObjects.length + ' Items.');

        Parse.Object.saveAll(unsavedItemObjects, {
            success: function (list) {
                // All the objects were saved.
                console.log('Successfully initialize ' + list.length + ' Items.');
                response.success(list.length);
            },
            error: function (error) {
                // An error occurred while saving one of the objects.
                console.log('An error occurred while saving one of the Item objects. Error code: ' + error.code + ' ' + error.message);
                response.error('An error occurred while saving one of the Item objects. Error code: ' + error.code + ' ' + error.message);
            }
        });

    }, function (error) {
        console.log('Failed to find initial Items. Error code: ' + error.code + ' ' + error.message);
        response.error("Failed to find initial Items. Error code: " + error.code + ": " + error.message);
    });

});

Parse.Cloud.define("initializeStoreMap", function (request, response) {
    var storeID = request.params.storeID;
    console.log('Start initializeStoreMap for storeID = ' + storeID);

    var storesObjects = Parse.Object.extend("Stores");
    var storeQuery = new Parse.Query(storesObjects);

    storeQuery.get(storeID, {
        success: function (store) {
            // The store was retrieved successfully.

            // get the default location
            var locationObjects = Parse.Object.extend("Locations");
            var locationsQuery = new Parse.Query(locationObjects);
            locationsQuery.addAscending("locationID");
            locationsQuery.find({
                success: function (locations) {
                    console.log('Found ' + locations.length + ' Locations.');
                    var defaultLocation = locations[0];

                    // get all groups
                    var queryGroups = new Parse.Query("Groups");

                    queryGroups.find({
                        success: function (groups) {
                            console.log('Found ' + groups.length + ' Groups.');
                            var user = request.user;
                            var ACL = new Parse.ACL(user);
                            ACL.setPublicReadAccess(true);
                            ACL.setPublicWriteAccess(true);
                            var unsavedStoreMapObjects = [];
                            for (var i = 0; i < groups.length; ++i) {
                                var map = new Parse.Object("StoreMaps");
                                map.set('author', user);
                                map.set('store', store);
                                map.set('group', groups[i]);
                                map.set('location', defaultLocation);
                                map.setACL(ACL);
                                unsavedStoreMapObjects[i] = map;
                            }
                            console.log('Ready to save ' + unsavedStoreMapObjects.length + ' map objects for storeID = ' + storeID);
                            Parse.Object.saveAll(unsavedStoreMapObjects, {
                                success: function (list) {
                                    // All the objects were saved.
                                    console.log('Successfully initialize ' + list.length + ' StoreMaps objects for storeID = ' + storeID);
                                    response.success('Successfully initialize ' + list.length + ' StoreMaps objects for storeID = ' + storeID);
                                },
                                error: function (error) {
                                    // An error occurred while saving one of the objects.
                                    console.log('An error occurred while saving one of the StoreMaps objects. Error code: ' + error.code + ' ' + error.message);
                                    response.error('An error occurred while saving one of the StoreMaps objects. Error code: ' + error.code + ' ' + error.message);
                                }
                            });
                        },
                        error: function (error) {
                            console.log('initializeStoreMap: Groups lookup failed. Error code: ' + error.code + ' ' + error.message);
                            response.error('initializeStoreMap: Groups lookup failed. Error code: ' + error.code + ' ' + error.message);
                        }
                    });


                },
                error: function (error) {
                    console.log('initializeStoreMap: Locations lookup failed. Error code: ' + error.code + ' ' + error.message);
                    response.error('initializeStoreMap: Locations lookup failed. Error code: ' + error.code + ' ' + error.message);
                }
            });

        },
        error: function (object, error) {
            // The store was not retrieved successfully.
            console.log('initializeStoreMap: Store lookup failed. Error code: ' + error.code + ' ' + error.message);
            response.error('initializeStoreMap: Store lookup failed. Error code: ' + error.code + ' ' + error.message);
        }
    });


});


Parse.Cloud.beforeSave("Stores", function (request, response) {
    console.log("Stores: beforeSave");
    var address1 = request.object.get("address1");
    var address2 = request.object.get("address2");
    var city = request.object.get("city");
    var state = request.object.get("state");
    var zip = request.object.get("zip");
    var country = request.object.get("country");
    var address = address1 + ',';
    if (address2 != '') {
        address = address + address2 + ',';
    }
    address = address + city + ',' + state + ',' + zip + ',' + country;
    address = encodeURIComponent(address.trim());

    Parse.Cloud.httpRequest({
        url: 'https://maps.googleapis.com/maps/api/geocode/json?address=' + address + ':ES&key=AIzaSyAebBA9NuwNy3K_aUEdVl3XoFArrc4vDs0'
    }).then(function (httpResponse) {
        // success
        var jsonObject = JSON.parse(httpResponse.text);
        var geocodeStatus = jsonObject.status;
        console.log('httpRequest success: geocodeStatus:' + geocodeStatus);
        if (geocodeStatus.substring(0, 2) == "OK") {
            var latitude = jsonObject.results[0].geometry.location.lat;
            var longitude = jsonObject.results[0].geometry.location.lng;
            console.log('latitude = ' + latitude + '; longitude = ' + longitude);

            var point = new Parse.GeoPoint({latitude: latitude, longitude: longitude});
            //TODO: Determine if the store already exists in the Stores table
            // Find the closes store to the calculated point.
            // then if the distance is within a minimum distance it exists
            request.object.set("location", point);

            response.success();
        } else {
            console.log('Failure: geocodeStatus NOT OK');
            response.error('Failure: geocodeStatus NOT OK');
        }
    }, function (httpResponse) {
        // error
        console.log('Error: Request failed with response code ' + httpResponse.status);
        response.error('Error: Request failed with response code ' + httpResponse.status);
    });

});

Parse.Cloud.afterSave("Stores", function (request) {
    var storeID = request.object.id;
    console.log('Start Stores: afterSave; storeID = ' + storeID);
    var store = request.object;

    var StoreMapsObject = Parse.Object.extend("StoreMaps");
    var storeMapsQuery = new Parse.Query(StoreMapsObject);
    storeMapsQuery.equalTo("store", store);
    storeMapsQuery.limit(50);

    storeMapsQuery.find().then(function (results) {
        console.log('Found ' + results.length + ' map entries for store = ' + storeID);
        if (results.length == 0) {
            console.log('No map entries found for storeID = ' + storeID + '. initializeStoreMap');
            Parse.Cloud.run('initializeStoreMap', {storeID: storeID}, {
                success: function () {
                    console.log('Stores:afterSave; initializeStoreMap Success.');
                },
                error: function (error) {
                    console.log('Stores:afterSave; initializeStoreMap FAIL: ');
                }
            });

        } else {
            console.log('Store found in the StoreMaps table. Do NOT initializeStoreMap');
        }

    }, function (error) {
        console.log('ERROR storeMapsQuery. Error code: ' + error.code + ' ' + error.message);
    });
});

	
	

	
	


