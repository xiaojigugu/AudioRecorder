
cmake_minimum_required(VERSION 3.4.1)

#当前版本下，默认目录为src/main/cpp/
#file(GLOB cpp_files /encoder/*.cpp)
file(GLOB cpp_files_main *.cpp)
add_library(
        native-lib
        SHARED
        ${cpp_files_main})

#find_library(
#        log-lib
#        log)

include_directories(include)

#添加静态库的一种方式（动态库则将STATIC修改为SHARED，.a修改为.so,动态库有版本兼容问题）
#另一种就是调用set()直接修改CMAKE_CXX_FLAGS参数
#add_library(
#        mp3lame
#        SHARED
#        IMPORTED)
#set_target_properties(
#        avcodec
#        PROPERTIES
#        IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.a
#)

# ANDROID_ABI也可以为CMAKE_ANDROID_ARCH_ABI
set(my_lib_path ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${my_lib_path}")

target_link_libraries(
        native-lib
        mp3lame
        #avfilter avformat avcodec avutil swresample swscale
        #-Wl,--start-group
        # avformat avcodec avutil avfilter swresample swscale
        #-Wl,--end-group
        z
        #OpenSLES
        android
        log
)