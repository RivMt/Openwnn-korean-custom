#include <jni.h>
#include <stdio.h>
#include <iostream>
#include <cstdlib>
#include <cstring>
#include <list>

#include "triedictionary-lib.h"

extern "C" {

TrieNode::TrieNode(wchar_t ch) {
    this->ch = ch;
    this->frequency = 0;
    this->children = nullptr;
    this->compressed = nullptr;
}

TrieNode * root;

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_initNative(JNIEnv * jenv, jobject self) {
    root = new TrieNode(L'\0');
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_searchNative(JNIEnv * jenv, jobject self, jstring word_) {

}

TrieNode * searchNode(std::wstring s) {
    TrieNode * p = root;
    return p;
}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_getAllWordsNative(JNIEnv * jenv, jobject self) {
    std::list<std::wstring> * list = getAllWords(root, new std::list<std::wstring>(), std::wstring(L""));
    jobjectArray result = jenv->NewObjectArray((jsize) list->size(), jenv->FindClass("java/lang/String"), jenv->NewStringUTF(""));
    for(auto it = list->begin() ; it != list->end() ; it++) {
        char str[it->length() + 1];
        wcstombs(str, it->c_str(), it->length() + 1);
        jenv->SetObjectArrayElement(result, (jsize) std::distance(list->begin(), it), jenv->NewStringUTF(str));
    }
    return result;
}

std::list<std::wstring> * getAllWords(TrieNode * p, std::list<std::wstring> * list, std::wstring currentWord) {
    if(p->frequency > 0) list->push_back(currentWord);
    if(p->children == nullptr) return list;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        TrieNode * child = it->second;
        getAllWords(child, list, currentWord + L"" + std::wstring(1, it->first));
    }
    return list;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_insertNative(JNIEnv * jenv, jobject self, jstring word_, jint frequency) {
    const char * chars = jenv->GetStringUTFChars(word_, JNI_FALSE);
    size_t length = strlen(chars)/sizeof(wchar_t) + 1;
    wchar_t word[length];
    mbstowcs(word, chars, length);
    TrieNode * p = root;
    for(wchar_t c : word) {
        if(c == L'\0') break;
        if(p->children == nullptr) p->children = new std::map<wchar_t, TrieNode*>();
        if(p->children->count(c)) {
            p = p->children->find(c)->second;
        } else {
            TrieNode * child = new TrieNode(c);
            p->children->insert(std::make_pair(c, child));
            p = child;
        }
    }
    p->frequency = (int) frequency;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_compressNative(JNIEnv * jenv, jobject self) {

}

};
