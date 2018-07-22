cmake_minimum_required(VERSION 3.4.1)

add_library(map src/main/cpp/map.c)
add_library(list_node src/main/cpp/list_node.c)
add_library(list_iterator src/main/cpp/list_iterator.c)
add_library(list src/main/cpp/list.c)

include_directories(src/main/cpp/)

add_library(triedictionary-lib SHARED src/main/cpp/triedictionary-lib.c)

target_link_libraries(list list_node list_iterator)
target_link_libraries(triedictionary-lib map list)
