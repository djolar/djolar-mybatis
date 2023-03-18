.PHONY: release
release:
	./mvnw clean deploy -P release

.PHONY: test
test:
	./mvnw clean test

.PHONY: benchmark
benchmark:
	./mvnw jmh:benchmark
