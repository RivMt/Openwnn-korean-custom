
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

std::list<std::wstring> * getAllWords(TrieNode * p, std::list<std::wstring> * list, std::wstring currentWord);

};

#endif //OPENWNN_TRIEDICTIONARY_LIB_H
