import requests
import xml.etree.ElementTree as elemTree
import pymysql

db = pymysql.connect(
    user='allthattour',
    passwd='dhfeotxndj2023!@#',
    host='localhost',
    db='allthattour',
    charset='utf8'
)


def saveContinents():
    data = requests.get(
        'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=1&olanguage_id=9')
    Continent_feed = elemTree.fromstring(data.text)
    continents = Continent_feed.find('continents')
    for continent in continents.findall('./continent'):
        sql = f"select * from agoda_continent where continent_id={continent.find('continent_id').text};"
        cursor = db.cursor(pymysql.cursors.DictCursor)
        cursor.execute(sql)
        result = cursor.fetchall()
        if len(result) == 0:
            sql = f"insert into agoda_continent (continent_id, continent_name, continent_translated, active_hotels) values ({continent.find('continent_id').text}, '{continent.find('continent_name').text}', '{continent.find('continent_translated').text}', {continent.find('active_hotels').text})"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(sql)
            db.commit()
        # else:
        #     sql = f"update agoda_continent set continent_name='{continent.find('continent_name').text}', continent_translated='{continent.find('continent_translated').text}', active_hotels={continent.find('active_hotels').text} where continent_id={continent.find('continent_id').text}"
        #     cursor = db.cursor(pymysql.cursors.DictCursor)
        #     cursor.execute(sql)
        #     db.commit()


def saveCountries():
    data = requests.get(
        f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=2&olanguage_id=9')
    Continent_feed = elemTree.fromstring(data.text)
    continents = Continent_feed.find('countries')
    for continent in continents.findall('./country'):
        sql = f"insert into agoda_country (country_id, continent_id, country_name, country_translated, active_hotels, country_iso, country_iso2, longitude, latitude) values (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
        cursor = db.cursor(pymysql.cursors.DictCursor)
        cursor.execute(query=sql, args=(continent.find('country_id').text, continent.find('continent_id').text, continent.find('country_name').text, continent.find('country_translated').text, continent.find(
            'active_hotels').text, continent.find('country_iso').text, continent.find('country_iso2').text, continent.find('longitude').text, continent.find('latitude').text))
        db.commit()
        # else:
        #     sql = f"update agoda_country set continent_id=%s, country_name=%s, country_translated=%s, active_hotels=%s, country_iso=%s, country_iso2=%s, longitude=%s, latitude=%s where country_id=%s"
        #     cursor = db.cursor(pymysql.cursors.DictCursor)
        #     cursor.execute(query=sql, args=(continent.find('continent_id').text, continent.find('country_name').text, continent.find('country_translated').text, continent.find(
        #         'active_hotels').text, continent.find('country_iso').text, continent.find('country_iso2').text, continent.find('longitude').text, continent.find('latitude').text, continent.find('country_id').text))
        #     db.commit()


def saveCities():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=3&ocountry_id={counter}&olanguage_id=9')
        City_feed = elemTree.fromstring(data.text)
        cities = City_feed.find('cities')
        if cities is not None:
            for city in cities.findall('./city'):
                sql = f"insert into agoda_city (city_id, country_id, city_name, city_translated, active_hotels, longitude, latitude, no_area) values (%s, %s, %s, %s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                cursor.execute(query=sql, args=(city.find('city_id').text, city.find('country_id').text, city.find('city_name').text, city.find('city_translated').text, city.find(
                    'active_hotels').text, city.find('longitude').text, city.find('latitude').text, city.find('no_area').text))
                db.commit()
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 500:
            break
                # else:
                #     sql = f"update agoda_city set country_id=%s, city_name=%s, city_translated=%s, active_hotels=%s, longitude=%s, latitude=%s, no_area=%s where city_id=%s"
                #     cursor = db.cursor(pymysql.cursors.DictCursor)
                #     cursor.execute(query=sql, args=(city.find('country_id').text, city.find('city_name').text, city.find('city_translated').text, city.find(
                #         'active_hotels').text, city.find('longitude').text, city.find('latitude').text, city.find('no_area').text, city.find('city_id').text))
                #     db.commit()


def saveAreas():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=4&acity_id={counter}&alanguage_id=9')
        Area_feed = elemTree.fromstring(data.text)
        areas = Area_feed.find('areas')
        if areas is not None:
            for area in areas.findall('./area'):
                sql = f"insert into agoda_area (area_id, city_id, area_name, area_translated, active_hotels, longitude, latitude) values (%s, %s, %s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                cursor.execute(query=sql, args=(area.find('area_id').text, area.find('city_id').text, area.find('area_name').text, area.find('area_translated').text, area.find(
                    'active_hotels').text, area.find('longitude').text, area.find('latitude').text))
                db.commit()
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 737538:
            break
            # else:
            #     sql = f"update agoda_area set city_id=%s, area_name=%s, area_translated=%s, active_hotels=%s, longitude=%s, latitude=%s where area_id=%s"
            #     cursor = db.cursor(pymysql.cursors.DictCursor)
            #     cursor.execute(query=sql, args=(area.find('city_id').text, area.find('area_name').text, area.find('area_translated').text, area.find(
            #         'active_hotels').text, area.find('longitude').text, area.find('latitude').text, area.find('area_id').text))
            #     db.commit()
def saveHotels():
    counter = 56966
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mcity_id={counter}&olanguage_id=9&feed_id=5')
        Hotel_feed = elemTree.fromstring(data.text)
        hotels = Hotel_feed.find('hotels')
        if hotels is not None:
            for hotel in hotels.findall('./hotel'):
                sql = f"insert into agoda_hotel (hotel_id, continent_id, country_id, city_id, area_id, popularity_score, number_of_reviews, infant_age, children_age_from, children_age_to, min_guest_age, hotel_name, hotel_formerly_name, translated_name, longitude, latitude, hotel_url, remark, accommodation_type, nationality_restrictions, star_rating, rating_average, children_stay_free, single_room_property) values (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                if hotel.find('child_and_extra_bed_policy').find('children_stay_free').text == 'True':
                    children_stay_free = 1
                else:
                    children_stay_free = 0
                if hotel.find('single_room_property').text == 'True':
                    single_room_property = 1
                else:
                    single_room_property = 0
                try:
                    cursor.execute(query=sql, args=(hotel.find('hotel_id').text, hotel.find('continent_id').text, hotel.find('country_id').text, hotel.find('city_id').text, hotel.find('area_id').text, hotel.find('popularity_score').text, hotel.find('number_of_reviews').text, hotel.find('child_and_extra_bed_policy').find('infant_age').text, hotel.find('child_and_extra_bed_policy').find('children_age_from').text, hotel.find('child_and_extra_bed_policy').find('children_age_to').text, hotel.find('child_and_extra_bed_policy').find('min_guest_age').text, hotel.find('hotel_name').text, hotel.find('hotel_formerly_name').text, hotel.find('translated_name').text, hotel.find('longitude').text, hotel.find('latitude').text, hotel.find('hotel_url').text, hotel.find('remark').text, hotel.find('accommodation_type').text, hotel.find('nationality_restrictions').text, hotel.find('star_rating').text, hotel.find('rating_average').text, children_stay_free, single_room_property))
                    db.commit()
                except:
                    pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1

def savePictures():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mhotel_id={counter}&feed_id=7&olanguage_id=9')
        Picture_feed = elemTree.fromstring(data.text)
        pictures = Picture_feed.find('pictures')
        if pictures is not None:
            for picture in pictures.findall('./picture'):
                sql = f"insert into agoda_picture (hotel_id, picture_id, caption, caption_translated, url) values (%s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                try:
                    cursor.execute(query=sql, args=(picture.find('hotel_id').text, picture.find('picture_id').text, picture.find('caption').text, picture.find('caption_translated').text, picture.find('URL').text))
                    db.commit()
                except:
                    pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 41527573:
            break

def saveRoomTypes():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mhotel_id={counter}&feed_id=6&olanguage_id=9')
        Roomtype_feed = elemTree.fromstring(data.text)
        roomtypes = Roomtype_feed.find('roomtypes')
        if roomtypes is not None:
            for roomtype in roomtypes.findall('./roomtype'):
                sql = f"insert into agoda_room_type (hotel_id, hotel_room_type_id, max_occupancy_per_room, no_of_room, size_of_room, max_extrabeds, max_infant_in_room, hotel_master_room_type_id, standard_caption, standard_caption_translated, views, hotel_room_type_picture, bed_type, room_size_in_incl_terrace, shared_bathroom, gender) values (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                if roomtype.find('shared_bathroom').text == 'True':
                    shared_bathroom = 1
                else:
                    shared_bathroom = 0
                try:
                    cursor.execute(query=sql, args=(roomtype.find('hotel_id').text, roomtype.find('hotel_room_type_id').text, roomtype.find('standard_caption').text, roomtype.find('standard_caption_translated').text, roomtype.find('max_occupancy_per_room').text, roomtype.find('no_of_room').text, roomtype.find('size_of_room').text, roomtype.find('room_size_incl_terrace').text, roomtype.find('views').text, roomtype.find('max_extrabeds').text, roomtype.find('max_infant_in_room').text, roomtype.find('hotel_room_type_picture').text, roomtype.find('bed_type').text, roomtype.find('hotel_master_room_type_id').text, roomtype.find('hotel_room_type_alternate_name').text, shared_bathroom, roomtype.find('gender').text))
                    db.commit()
                except:
                    pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 41527573:
            break

def saveFacilities():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mhotel_id={counter}&feed_id=9&olanguage=9')
        Facility_feed = elemTree.fromstring(data.text)
        facilities = Facility_feed.find('facilities')
        if facilities is not None:
            for facility in facilities.findall('./facility'):
                sql = f"insert into agoda_facility (hotel_id, property_id, property_group_description, property_name, property_translated_name) values (%s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                try:
                    cursor.execute(query=sql, args=(facility.find('hotel_id').text, facility.find('property_id').text, facility.find('property_group_description').text, facility.find('property_name').text, facility.find('property_translated_name').text))
                    db.commit()
                except:
                    pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 41527573:
            break

def saveHotelInfos():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mhotel_id={counter}&feed_id=10&olanguage=9')
        Hotelinfo_feed = elemTree.fromstring(data.text)
        hotelinfos = Hotelinfo_feed.find('hotelinfos')
        if hotelinfos is not None:
            for hotelinfo in hotelinfos.findall('./hotelinfo'):
                sql = f"insert into agoda_hotel_info (hotel_id, property_id, property_name, property_translated_name, property_details) values (%s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                try:
                    cursor.execute(query=sql, args=(hotelinfo.find('hotel_id').text, hotelinfo.find('property_id').text, hotelinfo.find('property_name').text, hotelinfo.find('property_translated_name').text, hotelinfo.find('property_details').text))
                    db.commit()
                except:
                    pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 41527573:
            break

def saveRoomTypeFacilities():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mhotel_id={counter}&feed_id=14')
        Roomtype_facility_feed = elemTree.fromstring(data.text)
        roomtype_facilities = Roomtype_facility_feed.find('roomtype_facilities')
        if roomtype_facilities is not None:
            for roomtype_facility in roomtype_facilities.findall('./roomtype_facility'):
                sql = f"insert into agoda_room_type_facility (hotel_id, hotel_room_type_id, property_id, property_name, translated_name) values (%s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                # try:
                cursor.execute(query=sql, args=(roomtype_facility.find('hotel_id').text, roomtype_facility.find('hotel_room_type_id').text, roomtype_facility.find('property_id').text, roomtype_facility.find('property_name').text, roomtype_facility.find('translated_name').text))
                db.commit()
                # except:
                #     pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 41527573:
            break

def saveHotelAddresses():
    counter = 1
    while True:
        print(f'{counter} - START')
        s = 0;
        data = requests.get(
            f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&mcity_id={counter}&feed_id=18&olanguage=9')
        Hotel_address_feed = elemTree.fromstring(data.text)
        hotel_addresses = Hotel_address_feed.find('hotel_addresses')
        if hotel_addresses is not None:
            for hotel_address in hotel_addresses.findall('./hotel_address'):
                sql = f"insert into agoda_hotel_address (hotel_id, address_type, address_line1, address_line2, postal_code, state, city, country) values (%s, %s, %s, %s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                try:
                    cursor.execute(query=sql, args=(hotel_address.find('hotel_id').text, hotel_address.find('address_type').text, hotel_address.find('address_line_1').text, hotel_address.find('address_line_2').text, hotel_address.find('postal_code').text, hotel_address.find('state').text, hotel_address.find('city').text, hotel_address.find('country').text))
                    db.commit()
                except:
                    pass
                s += 1
        print(f'{counter} - END (sum : {s})')
        counter += 1
        if counter > 737538:
            break

# saveContinents()
# saveCountries()
# saveCities()
# saveAreas()
saveHotels()
# savePictures()
# saveRoomTypes()
# saveFacilities()
# saveHotelInfos()
# saveRoomTypeFacilities()
# saveHotelAddresses()