#!/usr/bin/env python3

import argparse
import re
import os
import sys


def get_script_path():
    return os.path.dirname(os.path.realpath(sys.argv[0]))


def main():
    parser = argparse.ArgumentParser(
        description='Generate the font map for Material Design Icons.')
    parser.add_argument('input', type=str,
                        help='the minified css file containing the mapping for the mdi font')
    args = parser.parse_args()

    regex = r'\.mdi-([a-z\-0-9]+):before\{content:"\\([A-F0-9]{2,4})"\}'

    with open(args.input, 'r') as f_in:
        content = f_in.read()

    with open(get_script_path() + "/src/main/assets/mdi_map.txt", 'w') as f_out:
        for res in re.finditer(regex, content):
            name = res.group(1)
            hex_char = res.group(2).ljust(4, "0")
            char = bytes.fromhex(hex_char)
            str_char = bytes.decode(char, 'utf-16-be')

            f_out.write(name + " " + str_char + "\n")


if __name__ == "__main__":
    main()
