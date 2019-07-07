# -*- coding: utf-8 -*-
"""
@author: David
"""

from bs4 import BeautifulSoup
import requests
import re
from datetime import datetime
import json


data= {}
company ={'AAPL':'https://finance.yahoo.com/quote/AAPL/history?period1=1534132800&period2=1536811200&interval=1d&filter=history&frequency=1d',
          'TSLA':'https://finance.yahoo.com/quote/TSLA/history?period1=1534132800&period2=1536811200&interval=1d&filter=history&frequency=1d',
          'CRM':'https://finance.yahoo.com/quote/CRM/history?period1=1534132800&period2=1536811200&interval=1d&filter=history&frequency=1d',
          'S&P':'https://finance.yahoo.com/quote/SPY/history?period1=1534132800&period2=1536811200&interval=1d&filter=history&frequency=1d',
          'NFLX':'https://finance.yahoo.com/quote/NFLX/history?period1=1534132800&period2=1536811200&interval=1d&filter=history&frequency=1d'}
          
for key,value in company.items():
    page_link = value
#page_link = 'https://finance.yahoo.com/quote/AAPL/history?p=AAPL'
    page_response = requests.get(page_link, timeout=5)
    soup = BeautifulSoup(page_response.content, "html.parser")

    x = (soup.find_all("script"))
    y = x[-3]
    y= str(y)
    date_time = []
    time = re.findall('\{\"date\":([0-9]*?),\"',y)
    for t in time:
        date_time.append(datetime.fromtimestamp(int(t)).strftime("%A, %B %d, %Y %I:%M:%S"))
    open_price = re.findall('open\":(.*?),\"',y)
    close_price = re.findall('\"close\":(.*?),\"volume',y)
    open_price = open_price[1:]
    data[key] = [date_time,time,open_price,close_price]

#m = re.findall('\{"date".*\}', y)
#m = re.findall('\{\"date(.*?)\}', y)
"""
time[0] = Set 13
time[len(time)-1] = Aug 13
each day is 86400 
in seconds format 
""" 
#z = re.findall(pattern_s,y)
#print(z)
#result = [m.start() for m in re.finditer('\{"date".*\}', y)]

with open('data.txt', 'w') as outfile:  
    json.dump(data, outfile)