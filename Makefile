ANDROID_PLATFORM := 35
#APP_VER := 0.1.0
#APP_VERCODE := 1

AAPTLDFLAGS += --target-sdk-version 35
#AAPTLDFLAGS += --version-name $(APP_VER)
#AAPTLDFLAGS += --version-code $(APP_VERCODE)
BYTECODE_VER := 17
#DXFLAGS += --release

#CFLAGS += -flto
CFLAGS += -O2 -Wall -Wextra
#CFLAGS += -Werror
#LDFLAGS += -flto
#LDFLAGS += -s

#EXTRALIBS += lib/$(ANDROID_ABI)/libfoo.so

#KEYS := $(KEYSTORE)
#SIGNFLAGS := --ks $(KEYSTORE)

all: signed.apk

include build_common.mk

clean:
	$(call clean_classes)
	rm -f *.zip *.apk *.dex *.idsig \
		$(DEPS) $(OBJS) $(JNILIB)

.PHONY: all clean
