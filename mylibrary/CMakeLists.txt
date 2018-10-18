# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to  your build.
# 指定cmake版本
cmake_minimum_required(VERSION 3.4.1)

# 指定项目的名称，一般和项目的文件夹名称对应
# project(Chest)

# Specifies a path to native header files.
# 头文件目录，当使用 add_library()，将一个源文件（source file）或库添加到你的 CMake 构建脚本，
# 同步你的项目，然后你会发现 Android studio 将关联的头文件也显示了处理。
# 然而，为了让 CMake 在编译时期能定位到你的头文件，
# 你需要在 CMake 构建脚本中添加 include_directories()命令，并指定头文件路径：
include_directories(src/main/cpp/)

# 源文件目录
# aux_source_directorydir(cpp DIR_SRCS)

# 设置环境变量，编译用到的源文件全部都要放到这里，否则编译能够通过，但是执行的时候会出现各种问题，
# 比如"symbol lookup error xxxxx , undefined symbol"
# SET(TEST_MATH ${DIR_SRCS})

# 添加要编译的可执行文件
# ADD_EXECUTABLE(${PROJECT_NAME} ${TEST_MATH})

# 添加可执行文件所需要的库，比如我们用到了libm.so（命名规则：lib+name+.so），就添加该库的名称
# TARGET_LINK_LIBRARIES(${PROJECT_NAME} m)

# Specifies a library name, specifies whether the library is STATIC or  SHARED,
# and provides relative paths to the source code.
# You can  define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app,
# Gradle  automatically packages shared libraries with your APK.
add_library(serial_port SHARED src/main/cpp/SerialPort.c)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       serial_port

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

# 添加需要链接的库文件路径LINK_LIBRARIES
# link_libraries(log)


