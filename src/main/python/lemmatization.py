from nltk.stem import WordNetLemmatizer as wnl

print(wnl().lemmatize("went", pos="v")) # go
print(wnl().lemmatize("going", pos="v")) # go
