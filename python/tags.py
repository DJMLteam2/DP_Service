# response data def
def add_to_similar_tags(df, sorted_indices, cos_similarities, similar_tags):
    bag = []
    for index in sorted_indices:
        bag.append({
            "id": str(df.iloc[index]['SPOT_ID']),
            "area": str(df.iloc[index]['SPOT_CITY']),
            "title": str(df.iloc[index]['SPOT_TITLE']),
            "similarity": str(("{:.1f}".format(cos_similarities[0][index]*100))) + '%',
            "catchtitle": str(df.iloc[index]['SPOT_CATCHTITLE']),
            "detail": str(df.iloc[index]['SPOT_DETAIL']),
            "treatMenu": str(df.iloc[index]['SPOT_TREATMENU']),
            "tagName": str(df.iloc[index]['SPOT_TAGNAME']),
            "addr": str(df.iloc[index]['SPOT_ADDR']),
            "info": str(df.iloc[index]['SPOT_INFOCENTER']),
            # "lat": str(df.iloc[index]['parking']),
            "useTime": str(df.iloc[index]['SPOT_USETIME']),
            "conLike": str(df.iloc[index]['SPOT_CONLIKE']),
            "conRead": str(df.iloc[index]['SPOT_CONREAD']),
            "conShare": str(df.iloc[index]['SPOT_CONSHARE']),
            "imgPath": str(df.iloc[index]['SPOT_IMGPATH']),
            "overView": str(df.iloc[index]['SPOT_OVERVIEW'][:100]),
            "lat": str(df.iloc[index]['SPOT_LAT']),
            "lon": str(df.iloc[index]['SPOT_LON'])
            })
    similar_tags.append(bag)