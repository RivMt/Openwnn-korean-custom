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

Dictionary::Dictionary(int javaHash) {
    this->javaHash = javaHash;
    this->keyMap = nullptr;
    this->root = new TrieNode('\0');
}

std::map<int, Dictionary*> * dictionaries = new std::map<int, Dictionary*>();

const int getHashCode(JNIEnv * jenv, jobject obj) {
    jclass cls = jenv->GetObjectClass(obj);
    jmethodID mid = jenv->GetMethodID(cls, "hashCode", "()I");
    if(mid == 0) return 0;
    jint result = jenv->CallIntMethod(obj, mid);
    return (int) result;
}

TrieNode * getRoot(JNIEnv * jenv, jobject obj) {
    return dictionaries->find(getHashCode(jenv, obj))->second->root;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_initNative(JNIEnv * jenv, jobject self) {
    int hashCode = getHashCode(jenv, self);
    dictionaries->insert(std::make_pair(hashCode, new Dictionary(hashCode)));
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrieDictionary_setKeyMapNative(JNIEnv * jenv, jobject self, jobject keyMap_) {
    Dictionary * dictionary = dictionaries->find(getHashCode(jenv, self))->second;

    jclass hashMapClass = jenv->FindClass("java/util/HashMap");
    jmethodID hashMapEntrySet = jenv->GetMethodID(hashMapClass, "entrySet", "()Ljava/util/Set;");
    jclass setClass = jenv->FindClass("java/util/Set");
    jmethodID setIterator = jenv->GetMethodID(setClass, "iterator", "()Ljava/util/Iterator;");
    jclass iteratorClass = jenv->FindClass("java/util/Iterator");
    jmethodID iteratorHasNext = jenv->GetMethodID(iteratorClass, "hasNext", "()Z");
    jmethodID iteratorNext = jenv->GetMethodID(iteratorClass, "next", "()Ljava/lang/Object;");
    jclass mapEntryClass = jenv->FindClass("java/util/Map$Entry");
    jmethodID entryGetKey = jenv->GetMethodID(mapEntryClass, "getKey", "()Ljava/lang/Object;");
    jmethodID entryGetValue = jenv->GetMethodID(mapEntryClass, "getValue", "()Ljava/lang/Object;");

    jclass characterClass = jenv->FindClass("java/lang/Character");
    jmethodID characterGetValue = jenv->GetMethodID(characterClass, "charValue", "()C");

    jobject entrySet = jenv->CallObjectMethod(keyMap_, hashMapEntrySet);
    jobject iterator = jenv->CallObjectMethod(entrySet, setIterator);

    if(dictionary->keyMap != nullptr) delete dictionary->keyMap;
    dictionary->keyMap = new std::map<wchar_t, std::wstring*>();

    while(jenv->CallBooleanMethod(iterator, iteratorHasNext)) {
        jobject entry = jenv->CallObjectMethod(iterator, iteratorNext);
        jobject key = jenv->CallObjectMethod(entry, entryGetKey);
        jobject value = jenv->CallObjectMethod(entry, entryGetValue);

        wchar_t ch = (wchar_t) jenv->CallCharMethod(key, characterGetValue);
        const char * chars = jenv->GetStringUTFChars((jstring) value, JNI_FALSE);
        size_t length = strlen(chars) + 1;
        wchar_t * wchars = (wchar_t*) malloc(sizeof(wchar_t) * length);
        mbstowcs(wchars, chars, length);
        std::wstring * str = new std::wstring(wchars);

        dictionary->keyMap->insert(std::make_pair(ch, str));

        free(wchars);
        jenv->DeleteLocalRef(entry);
        jenv->DeleteLocalRef(key);
        jenv->DeleteLocalRef(value);
    }

    jenv->DeleteLocalRef(entrySet);
    jenv->DeleteLocalRef(iterator);

    jenv->DeleteLocalRef(hashMapClass);
    jenv->DeleteLocalRef(setClass);
    jenv->DeleteLocalRef(iteratorClass);
    jenv->DeleteLocalRef(mapEntryClass);
    jenv->DeleteLocalRef(characterClass);
}

std::map<std::wstring, int> * searchStroke(TrieNode * p, std::map<wchar_t, std::wstring*> * keyMap, std::wstring * stroke, std::wstring currentWord, std::map<std::wstring, int> * words, int depth, bool fitLength, int limit) {
    if(limit > 0 && depth > limit) return words;
    if(p->compressed != nullptr) {
        if(limit > 0 && depth + p->compressed->length() > limit) return words;
        bool match = false;
        for(wchar_t ch : p->compressed->substr(1)) {
            if(fitLength && depth >= stroke->length()-1) return words;
            std::wstring * charStroke;
            if(keyMap->count(ch)) charStroke = new std::wstring(keyMap->find(ch)->second->c_str());
            else charStroke = new std::wstring(1, ch);
            int j;
            for(j = 0 ; j < stroke->length() - depth ; j++) {
                if(charStroke->length() > j && stroke->length() > depth + j
                   && charStroke->at(j) == stroke->at(depth + j)) {
                    match = true;
                } else {
                    match = false;
                    break;
                }
            }
            delete charStroke;
            depth += j;
        }
        if(match) {
            words->insert(std::make_pair(currentWord + p->compressed->substr(1), p->frequency));
        }
        return words;
    }
    if(p->frequency > 0 && depth >= stroke->length()) {
        words->insert(std::make_pair(currentWord, p->frequency));
    }
    if(p->children == nullptr) return words;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        TrieNode * child = it->second;
        std::wstring * charStroke;
        wchar_t ch = child->ch;
        if(keyMap->count(ch)) charStroke = new std::wstring(keyMap->find(ch)->second->c_str());
        else charStroke = new std::wstring(1, ch);
        if(depth + charStroke->length() - 1 < stroke->length() && charStroke->at(0) == stroke->at(depth)) {
            bool check = true;
            int j;
            for(j = 1 ; j < charStroke->length() ; j++) {
                if(charStroke->at(j) != stroke->at(depth + j)) {
                    check = false;
                    break;
                }
            }
            if(check) searchStroke(child, keyMap, stroke, currentWord + ch, words, depth + j, fitLength, limit);
        } else if(!fitLength && depth + charStroke->length() > stroke->length()) {
            searchStroke(child, keyMap, stroke, currentWord + ch, words, depth + 1, fitLength, limit);
        }
        delete charStroke;
    }
    return words;
}

JNIEXPORT jobject JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrieDictionary_searchStrokeNative(JNIEnv * jenv, jobject self, jstring word_, jboolean fitLength, jint limit) {
    Dictionary * dictionary = dictionaries->find(getHashCode(jenv, self))->second;
    jclass hashMapClass = jenv->FindClass("java/util/HashMap");
    jmethodID hashMapInit = jenv->GetMethodID(hashMapClass, "<init>", "()V");
    jmethodID hashMapPut = jenv->GetMethodID(hashMapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    jclass integerClass = jenv->FindClass("java/lang/Integer");
    jmethodID integerInit = jenv->GetMethodID(integerClass, "<init>", "(I)V");

    const char * chars = jenv->GetStringUTFChars(word_, JNI_FALSE);
    size_t length = strlen(chars) + 1;
    wchar_t * word = (wchar_t*) malloc(sizeof(wchar_t) * length);
    mbstowcs(word, chars, length);
    std::wstring * stroke = new std::wstring(word);
    std::map<std::wstring, int> * words = searchStroke(dictionary->root, dictionary->keyMap, stroke, std::wstring(), new std::map<std::wstring, int>(), 0, fitLength, limit);
    jobject result = jenv->NewObject(hashMapClass, hashMapInit);
    for(auto it = words->begin() ; it != words->end() ; it++) {
        int index = (int) std::distance(words->begin(), it);
        if(index > 256) break;
        size_t len = it->first.length() * sizeof(wchar_t) + 1;
        char str[len];
        wcstombs(str, it->first.c_str(), len);
        jstring utfString = jenv->NewStringUTF(str);
        jobject integer = jenv->NewObject(integerClass, integerInit, it->second);
        jenv->CallObjectMethod(result, hashMapPut, utfString, integer);
        jenv->DeleteLocalRef(utfString);
        jenv->DeleteLocalRef(integer);
    }
    delete words;
    delete stroke;
    free(word);

    jenv->DeleteLocalRef(hashMapClass);
    jenv->DeleteLocalRef(integerClass);

    return result;
}

TrieNode * searchNode(TrieNode * root, wchar_t * word, size_t length) {
    TrieNode * p = root;
    for(int i = 0 ; i < length ; i++) {
        wchar_t c = word[i];
        if(c == L'\0') break;
        if(p->compressed != nullptr) return p;
        else if(p->children != nullptr && p->children->count(c)) p = p->children->find(c)->second;
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

std::map<std::wstring, int> * searchStartsWith(TrieNode * p, std::wstring prefix, std::map<std::wstring, int> * words, std::wstring currentWord, int depth, int limit) {
    if(prefix.length() <= 1) return words;
    if(limit > 0 && currentWord.length() > limit) return words;
    if(p->compressed != nullptr) {
        int length = (int) currentWord.length() + (int) p->compressed->length() - 1;
        if(limit > 0 && length > limit || prefix.length() > length) return words;
        if(prefix.length() > depth) {
            for(int j = 0 ; j < prefix.length() - depth ; j++) {
                if(p->compressed->substr(1).at(j) != prefix.at(depth + j)) return words;
            }
        }
        words->insert(std::make_pair(currentWord + p->compressed->substr(1), p->frequency));
        return words;
    }
    if(p->frequency > 0 && depth >= prefix.length()) {
        words->insert(std::make_pair(currentWord, p->frequency));
    }
    wchar_t ch = prefix[depth];
    if(p->children != nullptr) {
        if(p->children->count(ch)) {
            searchStartsWith(p->children->find(ch)->second, prefix, words, currentWord + ch, depth+1, limit);
        } else if(depth >= prefix.length()) {
            for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
                searchStartsWith(it->second, prefix, words, currentWord + it->second->ch, depth+1, limit);
            }
        }
    }
    return words;
}

JNIEXPORT jobject JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_searchStartsWithNative(JNIEnv * jenv, jobject self, jstring word_, jint limit) {
    jclass hashMapClass = jenv->FindClass("java/util/HashMap");
    jmethodID hashMapInit = jenv->GetMethodID(hashMapClass, "<init>", "()V");
    jmethodID hashMapPut = jenv->GetMethodID(hashMapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    jclass integerClass = jenv->FindClass("java/lang/Integer");
    jmethodID integerInit = jenv->GetMethodID(integerClass, "<init>", "(I)V");

    const char * chars = jenv->GetStringUTFChars(word_, JNI_FALSE);
    size_t length = strlen(chars) + 1;
    wchar_t * word = (wchar_t*) malloc(sizeof(wchar_t) * length);
    mbstowcs(word, chars, length);
    std::map<std::wstring, int> * words = searchStartsWith(getRoot(jenv, self), word, new std::map<std::wstring, int>(), std::wstring(), 0, 0);
    jobject result = jenv->NewObject(hashMapClass, hashMapInit);
    for(auto it = words->begin() ; it != words->end() ; it++) {
        int index = (int) std::distance(words->begin(), it);
        if(index > 256) break;
        size_t len = it->first.length() * sizeof(wchar_t) + 1;
        char str[len];
        wcstombs(str, it->first.c_str(), len);
        jstring utfString = jenv->NewStringUTF(str);
        jobject integer = jenv->NewObject(integerClass, integerInit, it->second);
        jenv->CallObjectMethod(result, hashMapPut, utfString, integer);
        jenv->DeleteLocalRef(utfString);
        jenv->DeleteLocalRef(integer);
    }
    delete words;
    free(word);

    jenv->DeleteLocalRef(hashMapClass);
    jenv->DeleteLocalRef(integerClass);

    return result;
}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_searchStrokeNative(JNIEnv * jenv, jobject self) {

}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_getAllWordsNative(JNIEnv * jenv, jobject self) {
    std::list<std::wstring> * list = getAllWords(getRoot(jenv, self), new std::list<std::wstring>(), std::wstring(0), 0);
    jobjectArray result = jenv->NewObjectArray((jsize) list->size(), jenv->FindClass("java/lang/String"), jenv->NewStringUTF(""));
    for(auto it = list->begin() ; it != list->end() ; it++) {
        char str[it->length() + 1];
        wcstombs(str, it->c_str(), it->length() + 1);
        jenv->SetObjectArrayElement(result, (jsize) std::distance(list->begin(), it), jenv->NewStringUTF(str));
    }
    return result;
}

std::list<std::wstring> * getAllWords(TrieNode * p, std::list<std::wstring> * list, std::wstring currentWord, int limit) {
    if(p->compressed != nullptr) {
        if(limit <= 0 || currentWord.length() + p->compressed->length() <= limit)
            list->push_back(currentWord + p->compressed->substr(1));
        return list;
    }
    if(p->frequency > 0) list->push_back(currentWord);
    if(p->children == nullptr) return list;
    if(limit > 0 && currentWord.length() > limit) return list;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        TrieNode * child = it->second;
        getAllWords(child, list, currentWord + it->first, limit);
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
            if(!p->children->count(c)) p->children->insert(std::make_pair(c, child));
            p = child;
        }
    }
    free(word);
    if(p->frequency == 0) p->frequency = (int) frequency;
}

TrieNode * compress(TrieNode * p) {
    if(p->children == nullptr) return p;
    for(auto it = p->children->begin() ; it != p->children->end() ; it++) {
        compress(it->second);
    }
    if(p->children->size() == 1) {
        TrieNode * child = p->children->begin()->second;
        if(child->children != nullptr) return p;
        if(child->compressed != nullptr) {
            p->compressed = new std::wstring(1, p->ch);
            p->compressed->append(*child->compressed);
            delete child->compressed;
            child->compressed = nullptr;
        } else {
            p->compressed = new std::wstring(1, p->ch);
            p->compressed->append(std::wstring(1, child->ch));
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
