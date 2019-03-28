from nltk import LancasterStemmer, TreebankWordTokenizer, PorterStemmer, wordpunct_tokenize

class CustomTokenizerC(object):
    def __call__(self, doc):
        stemmer=PorterStemmer()
        return [stemmer.stem(t) for t in wordpunct_tokenize(doc)]

class CustomTokenizerR(object):
    def __call__(self, doc):
        stemmer=LancasterStemmer()
        token=TreebankWordTokenizer()
        return [stemmer.stem(t) for t in token.tokenize(doc)]