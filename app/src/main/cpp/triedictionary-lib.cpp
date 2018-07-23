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

std::map<int, TrieNode*> * roots = new std::map<int, TrieNode*>();

const int getHashCode(JNIEnv * jenv, jobject obj) {
    jclass cls = jenv->GetObjectClass(obj);
    jmethodID mid = jenv->GetMethodID(cls, "hashCode", "()I");
    if(mid == 0) return 0;
    jint result = jenv->CallIntMethod(obj, mid);
    return (int) result;
}

TrieNode * getRoot(JNIEnv * jenv, jobject obj) {
    return roots->find(getHashCode(jenv, obj))->second;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_initNative(JNIEnv * jenv, jobject self) {
    roots->insert(std::make_pair(getHashCode(jenv, self), new TrieNode(L'\0')));
}

TrieNode * searchNode(TrieNode * root, wchar_t * word, size_t length) {
    TrieNode * p = root;
    for(int i = 0 ; i < length ; i++) {
        wchar_t c = word[i];
        if(c == L'\0') break;
        if(p->children != nullptr && p->children->count(c)) p = p->children->find(c)->second;
        else return nullptr;
    }
    if(p == root) return nullptr;
    return p;
}

JNIEXPORT jboolean JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_searchNative(JNIEnv * jenv, jobject self, jstring word_, jboolean fitLength) {
    const char * chars = jenv->GetStringUTFChars(word_, JNI_FALSE);
    size_t length = strlen(chars) + 1;
    wchar_t * word = (wchar_t*) malloc(sizeof(wchar_t) * length);
    mbstowcs(word, chars, length);
    TrieNode * p = searchNode(getRoot(jenv, self), word, length);
    return (jboolean) (p != nullptr && (fitLength && p->frequency == 0));
}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_searchStartsWithNative(JNIEnv * jenv, jobject self, jstring word_, jint limit) {
    const char * chars = jenv->GetStringUTFChars(word_, JNI_FALSE);
    size_t length = strlen(chars) + 1;
    wchar_t * word = (wchar_t*) malloc(sizeof(wchar_t) * length);
    mbstowcs(word, chars, length);
    TrieNode * p = searchNode(getRoot(jenv, self), word, length);
    if(p != nullptr) {
        std::list<std::wstring> * list = getAllWords(p, new std::list<std::wstring>(), std::wstring(word), limit);
        jobjectArray result = jenv->NewObjectArray((jsize) list->size(), jenv->FindClass("java/lang/String"), jenv->NewStringUTF(""));
        for(auto it = list->begin() ; it != list->end() ; it++) {
            int index = (int) std::distance(list->begin(), it);
            if(index > 256) break;
            char str[it->length() + 1];
            wcstombs(str, it->c_str(), it->length() + 1);
            jenv->SetObjectArrayElement(result, (jsize) index, jenv->NewStringUTF(str));
        }
        delete list;
        free(word);
        return result;
    } else {
        jobjectArray result = jenv->NewObjectArray((jsize) 0, jenv->FindClass("java/lang/String"), jenv->NewStringUTF(""));
        free(word);
        return result;
    }
}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_searchStrokeNative(JNIEnv * jenv, jobject self) {

}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_getAllWordsNative(JNIEnv * jenv, jobject self) {
    std::list<std::wstring> * list = getAllWords(getRoot(jenv, self), new std::list<std::wstring>(), std::wstring(L""), 0);
    jobjectArray result = jenv->NewObjectArray((jsize) list->size(), jenv->FindClass("java/lang/String"), jenv->NewStringUTF(""));
    for(auto it = list->begin() ; it != list->end() ; it++) {
        char str[it->length() + 1];
        wcstombs(str, it->c_str(), it->length() + 1);
        jenv->SetObjectArrayElement(result, (jsize) std::distance(list->begin(), it), jenv->NewStringUTF(str));
    }
    return result;
}

std::list<std::wstring> * getAllWords(TrieNode * p, std::list<std::wstring> * list, std::wstring currentWord, int limit) {
    if(p->frequency > 0) list->push_back(currentWord);
    if(p->children == nullptr) return list;
    if(limit > 0 && currentWord.length() > limit) return list;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        TrieNode * child = it->second;
        getAllWords(child, list, currentWord + L"" + std::wstring(1, it->first), limit);
    }
    return list;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_insertNative(JNIEnv * jenv, jobject self, jstring word_, jint frequency) {
    const char * chars = jenv->GetStringUTFChars(word_, JNI_FALSE);
    size_t length = strlen(chars) + 1;
    wchar_t * word = (wchar_t*) malloc(sizeof(wchar_t) * length);
    mbstowcs(word, chars, length);
    TrieNode * p = getRoot(jenv, self);
    for(int i = 0 ; i < length ; i++) {
        wchar_t c = word[i];
        if(c == L'\0') break;
        if(p->children == nullptr) p->children = new std::map<wchar_t, TrieNode*>();
        if(p->children->count(c)) {
            p = p->children->find(c)->second;
        } else {
            TrieNode * child = new TrieNode(c);
            (*p->children)[c] = child;
            p = child;
        }
    }
    free(word);
    p->frequency = (int) frequency;
}

TrieNode * compress(TrieNode * p) {
    if(p->children == nullptr) return p;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        compress(it->second);
    }
    if(p->children->size() == 1) {
        TrieNode * child = p->children->begin()->second;
        if(child->compressed != nullptr) {
            p->compressed = new std::wstring(1, p->ch);
            p->compressed->append(*child->compressed);
            delete child->compressed;
            child->compressed = nullptr;
        } else {
            p->compressed = new std::wstring(1, p->ch);
            p->compressed->append(1, child->ch);
        }
        p->frequency = child->frequency;
        delete child->children;
        delete child;
        delete p->children;
        p->children = nullptr;
    }
    return p;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_compressNative(JNIEnv * jenv, jobject self) {
    compress(getRoot(jenv, self));
}

void deinit(TrieNode * p) {
    if(p->children == nullptr) return;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        deinit(it->second);
    }
    delete p->children;
    delete p->compressed;
    delete p;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_deinitNative(JNIEnv * jenv, jobject self) {
    deinit(getRoot(jenv, self));
}

};
