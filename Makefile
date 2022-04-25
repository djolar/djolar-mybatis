.PHONY: release
release:
	./mvnw clean deploy -P release -Dmaven.test.skip=true

.PHONY: test
test:
	./mvnw clean test
