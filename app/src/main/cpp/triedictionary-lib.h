
#ifndef OPENWNN_TRIEDICTIONARY_LIB_H
#define OPENWNN_TRIEDICTIONARY_LIB_H

#include <map>

extern "C" {

class TrieNode {
public:
    wchar_t ch;
    int frequency;
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
