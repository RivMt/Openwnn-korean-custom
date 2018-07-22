#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <wchar.h>
#include <android/asset_manager.h>
#include "list.h"
#include "map.h"

#define WORD_LENGTH 60

typedef struct TrieNode TrieNode;
typedef map_t(TrieNode*) trienode_map_t;
typedef struct Word Word;
typedef struct WordList WordList;

struct TrieNode {
    wchar_t ch;
    int frequency;
    trienode_map_t * children;
};

struct Word {
    char * word;
    int frequency;
};

struct WordList {
    list_t * list;
    int size;
};

TrieNode * createNode(wchar_t ch, int frequency);
void addChild(TrieNode *p, TrieNode *child);
TrieNode ** getChild(TrieNode *p, wchar_t ch);

TrieNode * root;

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_initNative(JNIEnv * jenv, jobject self) {
    root = createNode('\0', 0);
    root->children = (trienode_map_t*) malloc(sizeof(trienode_map_t));
    map_init(root->children);
}

void getAllWordsRecursive(TrieNode * p, WordList * list, char * currentWord) {
    const char * key;
    map_iter_t iterator;
    char * newString;
    Word word;
    list_node_t * node;

    if(p->frequency > 0) {
        strcat(currentWord, "\0");
        word.word = currentWord;
        word.frequency = p->frequency;
        node = list_node_new(&word);
        list_rpush(list->list, node);
        list->size++;
    }
    if(p->children == NULL) return;
    iterator = map_iter(p->children);
    while((key = map_next(p->children, &iterator))) {
        newString = (char*) malloc(sizeof(char) * WORD_LENGTH);
        strcpy(newString, currentWord);
        strcat(newString, key);
        getAllWordsRecursive(*map_get(p->children, key), list, newString);
    }
}

WordList getAllWords() {
    list_t * list;
    WordList result;
    list = list_new();
    result.list = list;
    getAllWordsRecursive(root, &result, "");
    return result;
}

JNIEXPORT jobjectArray JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_getAllWordsNative(JNIEnv * jenv, jobject self) {
    WordList list;
    jobjectArray result;
    int i;
    list_node_t node;
    Word * word;

    list = getAllWords();
    result = (*jenv)->NewObjectArray(jenv, list.size, (*jenv)->FindClass(jenv, "java/lang/String"), (*jenv)->NewStringUTF(jenv, ""));
    for(i = 0 ; i < list.size ; i++) {
        node = *list_at(list.list, i);
        word = node.val;
        (*jenv)->SetObjectArrayElement(jenv, result, i, (*jenv)->NewStringUTF(jenv, word->word));
    }
    return result;
}

void insertWord(wchar_t * word, int frequency) {
    TrieNode * p;
    TrieNode ** check;
    TrieNode * child;
    int i;

    p = root;
    for(i = 0 ; i < WORD_LENGTH ; i++) {
        if(word[i] == '\0') break;
        if(p->children == NULL) {
            p->children = (trienode_map_t*) malloc(sizeof(trienode_map_t));
            map_init(p->children);
        }
        check = getChild(p, word[i]);
        if(!check) {
            child = createNode(word[i], 0);
            addChild(p, child);
        } else {
            child = *check;
        }
        p = child;
    }
    p->frequency = frequency;
}

JNIEXPORT void JNICALL
Java_me_blog_hgl1002_openwnn_KOKR_trie_NativeTrie_insertNative(JNIEnv * jenv, jobject self, jstring _word, jint frequency) {
    wchar_t word[WORD_LENGTH];
    mbstowcs(word, (*jenv)->GetStringUTFChars(jenv, _word, JNI_FALSE), WORD_LENGTH);
    insertWord(word, frequency);
}

TrieNode * compressRecursive(TrieNode * p) {
    int i;

    if(p->children == NULL) return p;
    return p;
}

JNIEXPORT jstring JNICALL
Java_me_blog_hgl1002_openwnn_OpenWnnKOKR_testFunction(JNIEnv * jenv, jobject self) {
    wchar_t ch = L'a';
    char msg[60];
    sprintf(msg, "Hello, %c", ch);
    return (*jenv)->NewStringUTF(jenv, msg);
}

TrieNode * createNode(wchar_t ch, int frequency) {
    TrieNode * p = (TrieNode*) malloc(sizeof(TrieNode));
    p->ch = ch;
    p->frequency = frequency;
    p->children = NULL;
    return p;
}

void addChild(TrieNode *p, TrieNode *child) {
    char str[3];
    wcstombs(str, &child->ch, 3);

    map_set(p->children, str, child);
}

TrieNode ** getChild(TrieNode *p, wchar_t ch) {
    char str[3];
    wcstombs(str, &ch, 3);

    return map_get(p->children, str);
}
