import sys
import argparse
import traceback


if __name__ == "__main__":
	desc = '''
	Simple implementation of Dijkstra's shortest-path algorithm.
	'''

	parser = argparse.ArgumentParser(prog='dijkstra', formatter_class=argparse.RawDescriptionHelpFormatter, description=desc)
    parser.add_argument('--summary', default=False, action="store_true", help="Print a summary of the naming conformance results")
    parser.add_argument('--finegrain', default=False, action="store_true", help="Print the finegrain naming conformance results.")
    parser.add_argument('-v', '--verbose', default=False, action="store_true", required=False, help="Print verbose results highlighting reasons for naming result discrepancies")
    parser.add_argument('-d', '--debug', default=False, action="store_true", required=False, help="Enable debug mode (only use if you encounter an error)")
    args = parser.parse_args()

