import requests
import xml.etree.ElementTree as elemTree
import pymysql

db = pymysql.connect(
    user='allthattour',
    passwd='dhfeotxndj2023!@#',
    host='211.203.51.244',
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
            sql = f"insert into agoda_continent values ({continent.find('continent_id').text}, '{continent.find('continent_name').text}', '{continent.find('continent_translated').text}', {continent.find('active_hotels').text})"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(sql)
            db.commit()
        else:
            sql = f"update agoda_continent set continent_name='{continent.find('continent_name').text}', continent_translated='{continent.find('continent_translated').text}', active_hotels={continent.find('active_hotels').text} where continent_id={continent.find('continent_id').text}"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(sql)
            db.commit()


def saveCountries():
    data = requests.get(
        'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=2&olanguage_id=9')
    Continent_feed = elemTree.fromstring(data.text)
    continents = Continent_feed.find('countries')
    for continent in continents.findall('./country'):
        sql = f"select * from agoda_country where country_id={continent.find('country_id').text};"
        cursor = db.cursor(pymysql.cursors.DictCursor)
        cursor.execute(sql)
        result = cursor.fetchall()
        if len(result) == 0:
            sql = f"insert into agoda_country values (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(query=sql, args=(continent.find('country_id').text, continent.find('continent_id').text, continent.find('country_name').text, continent.find('country_translated').text, continent.find(
                'active_hotels').text, continent.find('country_iso').text, continent.find('country_iso2').text, continent.find('longitude').text, continent.find('latitude').text))
            db.commit()
        else:
            sql = f"update agoda_country set continent_id=%s, country_name=%s, country_translated=%s, active_hotels=%s, country_iso=%s, country_iso2=%s, longitude=%s, latitude=%s where country_id=%s"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(query=sql, args=(continent.find('continent_id').text, continent.find('country_name').text, continent.find('country_translated').text, continent.find(
                'active_hotels').text, continent.find('country_iso').text, continent.find('country_iso2').text, continent.find('longitude').text, continent.find('latitude').text, continent.find('country_id').text))
            db.commit()
        saveCities(continent.find('country_id').text)


def saveCities(id):
    data = requests.get(
        f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=3&ocountry_id={id}&olanguage_id=9')
    City_feed = elemTree.fromstring(data.text)
    cities = City_feed.find('cities')
    if cities is not None:
        for city in cities.findall('./city'):
            sql = f"select * from agoda_city where city_id={city.find('city_id').text};"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(sql)
            result = cursor.fetchall()
            print(city.find('city_id').text)
            if len(result) == 0:
                sql = f"insert into agoda_city values (%s, %s, %s, %s, %s, %s, %s, %s)"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                cursor.execute(query=sql, args=(city.find('city_id').text, city.find('country_id').text, city.find('city_name').text, city.find('city_translated').text, city.find(
                    'active_hotels').text, city.find('longitude').text, city.find('latitude').text, city.find('no_area').text))
                db.commit()
            else:
                sql = f"update agoda_city set country_id=%s, city_name=%s, city_translated=%s, active_hotels=%s, longitude=%s, latitude=%s, no_area=%s where city_id=%s"
                cursor = db.cursor(pymysql.cursors.DictCursor)
                cursor.execute(query=sql, args=(city.find('country_id').text, city.find('city_name').text, city.find('city_translated').text, city.find(
                    'active_hotels').text, city.find('longitude').text, city.find('latitude').text, city.find('no_area').text, city.find('city_id').text))
                db.commit()
        saveAreas(city.find('city_id').text)


def saveAreas(id):
    data = requests.get(
        f'http://affiliatefeed.agoda.com/datafeeds/feed/getfeed?apikey=d3beca16-7cb7-490f-94e5-11c0a076dcf5&feed_id=4&acity_id={id}&alanguage_id=9')
    Area_feed = elemTree.fromstring(data.text)
    areas = Area_feed.find('areas')
    for area in areas.findall('./area'):
        sql = f"select * from agoda_area where area_id={area.find('area_id').text};"
        cursor = db.cursor(pymysql.cursors.DictCursor)
        cursor.execute(sql)
        result = cursor.fetchall()
        if len(result) == 0:
            sql = f"insert into agoda_area values (%s, %s, %s, %s, %s, %s, %s)"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(query=sql, args=(area.find('area_id').text, area.find('city_id').text, area.find('area_name').text, area.find('area_translated').text, area.find(
                'active_hotels').text, area.find('longitude').text, area.find('latitude').text))
            db.commit()
        else:
            sql = f"update agoda_area set city_id=%s, area_name=%s, area_translated=%s, active_hotels=%s, longitude=%s, latitude=%s where area_id=%s"
            cursor = db.cursor(pymysql.cursors.DictCursor)
            cursor.execute(query=sql, args=(area.find('city_id').text, area.find('area_name').text, area.find('area_translated').text, area.find(
                'active_hotels').text, area.find('longitude').text, area.find('latitude').text, area.find('area_id').text))
            db.commit()


saveContinents()
saveCountries()
