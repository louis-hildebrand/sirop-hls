.PHONY: simulation
simulation:
	./scripts/test_vhdl.sh . -v

.PHONY: synthesis
synthesis:
	number_of_qpfs="$$(find . -maxdepth 1 -type f -name '*.qpf' -printf '.' | wc -c)" && \
	[ "$$number_of_qpfs" -eq '1' ] || { echo "Found $$number_of_qpfs .qpf files"; exit 1; }
	quartus_sh --flow compile *.qpf

.PHONY: clean
clean:
	rm -rf lib modelsim.ini transcript vsim.wlf
