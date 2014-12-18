#!/usr/bin/python

import cPickle as pickle
import nltk.classify.util
from nltk.classify import NaiveBayesClassifier
from nltk.tokenize import word_tokenize
import sys
from nltk.corpus import movie_reviews
 
def word_feats(words):
    return dict([(word, True) for word in words])
 

sys.stderr.write("Started mapper.\n");

def word_feats(words):
    return dict([(word, True) for word in words])


def subj(subjLine, subj1):
    subjgen = subjLine.lower()
    # Replace term1 with your subject term    
    if subjgen.find(subj1) != -1:
        subject = subj1
        return subject
    else:
        subject = "No match"
        return subject


def main(argv):
    negids = movie_reviews.fileids('neg')
    posids = movie_reviews.fileids('pos')

    #print negids
 
    negfeats = [(word_feats(movie_reviews.words(fileids=[f])), 'negative') for f in negids]
    posfeats = [(word_feats(movie_reviews.words(fileids=[f])), 'positive') for f in posids]

    trainfeats =  posfeats+negfeats
    #print trainfeats
    #    break
    classifier = NaiveBayesClassifier.train(trainfeats)

    #classifier = pickle.load(open("classifier.p", "rb"))
    topicList = ["media", "sports", "news", "fashion", "finance", "politics"]
    for line in sys.stdin:
        try:
            tolk_posset = word_tokenize(line.rstrip())
            d = word_feats(tolk_posset)
            for topic in topicList:
                subjectFull = subj(line, topic)
                if not subjectFull == "No match":
                    #print d
                    print "LongValueSum:" + "" + str(line.split(":")[0])+","+subjectFull + "," + classifier.classify(d) + "\t" + "1"                    
        except:
                #print "Error"
                continue
        #    print "LongValueSum:" + " " + subjectFull + ": " + "\t" + "1"
        #else:
            


if __name__ == "__main__":
    main(sys.argv)
