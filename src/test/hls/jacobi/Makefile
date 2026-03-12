.PHONY: run-fpga
run-fpga: fpga
	./jacobi

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./jacobi.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./jacobi.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./jacobi.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./jacobi.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: jacobi.prj

jacobi.prj: jacobi.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl --clock 175MHz ./jacobi.cpp -o jacobi

.PHONY: run-cpu
run-cpu: cpu
	./jacobi-cpu

.PHONY: cpu
cpu: jacobi-cpu

jacobi-cpu: jacobi.cpp
	i++ -march='x86-64' ./jacobi.cpp -o jacobi-cpu

.PHONY: clean
clean:
	rm -rf jacobi.prj/ jacobi jacobi-cpu transcript
