
#ifndef OPENWNN_TRIEDICTIONARY_LIB_H
#define OPENWNN_TRIEDICTIONARY_LIB_H

#include <map>

extern "C" {

const int POS_NOUN = 101;
const int POS_ABBREVIATION = 102;
const int POS_NOUN_PROPER = 103;
const int POS_NOUN_NAME = 104;
const int POS_NOUN_SURNAME = 105;
const int POS_NOUN_ORGANIZATION = 106;
const int POS_NOUN_GEOLOCATION = 107;
const int POS_NOUN_ETC = 108;
const int POS_NOUN_USER = 109;

const int POS_ADJECTIVE = 200;
const int POS_ADVERB = 210;

const int POS_VERB = 300;
const int POS_VERB_PREFIX = 310;

const int POS_CONJUNCTION = 410;
const int POS_DETERMINER = 420;
const int POS_EXCLAMATION = 430;

const int POS_JOSA = 500;
const int POS_EOMI = 510;
const int POS_PRE_EOMI = 515;

class TrieNode {
public:
    wchar_t ch;
    int frequency;
    std::list<int> * pos;
    std::map<wchar_t, TrieNode*> * children;
    std::wstring * compressed;

    TrieNode(wchar_t ch);
};

class Dictionary {
public:
    int javaHash;
    TrieNode * root;
    std::map<wchar_t, std::wstring*> * keyMap;

    Dictionary(int javaHash);
};

std::list<std::wstring> * getAllWords(TrieNode * p, std::list<std::wstring> * list, std::wstring currentWord, int limit);

};

#endif //OPENWNN_TRIEDICTIONARY_LIB_H
